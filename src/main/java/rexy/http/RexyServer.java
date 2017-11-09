package rexy.http;

import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.model.Api;
import rexy.config.model.Config;
import rexy.feature.Feature;
import rexy.feature.FeatureInitialisationException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Creates a server with the {@link Api APIs} defined in the {@link Config configuration} with the supplied
 * {@link Feature features}. Each API is registered against the server as a different context.
 */
public class RexyServer {
	private static final Logger logger = LogManager.getLogger(RexyServer.class);
	
	private final HttpServer server;
	
	/**
	 * Creates a server and initialises the features for each API.
	 *
	 * @param config   The configuration containing the port, base URL and APIs
	 * @param features The features to register with the endpoints
	 * @throws IOException                    Thrown if the server cannot be created
	 * @throws FeatureInitialisationException Thrown if a feature cannot be initialised
	 */
	public RexyServer(Config config, List<Feature> features) throws IOException, FeatureInitialisationException {
		server = HttpServer.create(new InetSocketAddress(config.getPort()), 0);
		for (Api api : config.getApis()) {
			String apiEndpoint = config.getBaseUrl() + api.getBaseUrl();
			createEndpoing(features, api, apiEndpoint);
		}
		server.setExecutor(null); // creates a default executor
	}
	
	private void createEndpoing(List<Feature> features, Api api, String apiEndpoint)
			throws FeatureInitialisationException {
		logger.debug("Creating API endpoint for " + apiEndpoint);
		for (Feature feature : features) {
			feature.initEndpoint(api);
		}
		server.createContext(apiEndpoint, new RexyHandler(api, features));
		logger.info("API endpoint created for " + apiEndpoint);
	}
	
	/**
	 * Starts the HTTP server.
	 */
	public void start() {
		server.start();
	}
}
