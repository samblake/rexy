package rexy.http;

import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.model.Api;
import rexy.config.model.Config;
import rexy.module.Module;
import rexy.module.ModuleInitialisationException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Creates a server with the {@link Api APIs} defined in the {@link Config configuration} with the supplied
 * {@link Module modules}. Each API is registered against the server as a different context.
 */
public class RexyServer {
	private static final Logger logger = LogManager.getLogger(RexyServer.class);
	
	private final HttpServer server;
	
	/**
	 * Creates a server and initialises the modules for each API.
	 *
	 * @param config   The configuration containing the port, base URL and APIs
	 * @param modules The modules to register with the endpoints
	 * @throws IOException                    Thrown if the server cannot be created
	 * @throws ModuleInitialisationException Thrown if a module cannot be initialised
	 */
	public RexyServer(Config config, List<Module> modules) throws IOException, ModuleInitialisationException {
		server = HttpServer.create(new InetSocketAddress(config.getPort()), 0);
		for (Api api : config.getApis()) {
			String apiEndpoint = config.getBaseUrl() + api.getBaseUrl();
			createEndpoing(modules, api, apiEndpoint);
		}
		server.setExecutor(null); // creates a default executor
	}
	
	private void createEndpoing(List<Module> modules, Api api, String apiEndpoint)
			throws ModuleInitialisationException {
		logger.debug("Creating API endpoint for " + apiEndpoint);
		for (Module module : modules) {
			module.initEndpoint(api);
		}
		server.createContext(apiEndpoint, new RexyHandler(api, modules));
		logger.info("API endpoint created for " + apiEndpoint);
	}
	
	/**
	 * Starts the HTTP server.
	 */
	public void start() {
		server.start();
	}
	
}
