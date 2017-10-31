package rexy.http;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rexy.config.Api;
import rexy.config.Config;
import rexy.feature.Feature;
import rexy.feature.FeatureInitialisationException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class Server {
	private static final Logger logger = LoggerFactory.getLogger(Server.class);

	private final HttpServer server;

	public Server(Config config, List<Feature> features) throws IOException, FeatureInitialisationException {
		server = HttpServer.create(new InetSocketAddress(config.getPort()), 0);
		for (Api api : config.getApis()) {
			String apiEndpoint = config.getBaseUrl() + api.getBaseUrl();
			createEndpoing(features, api, apiEndpoint);
		}
		server.setExecutor(null); // creates a default executor
	}
	
	private void createEndpoing(List<Feature> features, Api api, String apiEndpoint) throws FeatureInitialisationException {
		logger.debug("Creating API endpoint for " + apiEndpoint);
		for (Feature feature : features) {
			feature.endpointCreation(api);
		}
		server.createContext(apiEndpoint, new RexyHandler(api, features));
		logger.info("API endpoint created for " + apiEndpoint);
	}
	
	public void start() throws IOException {
		server.start();
	}
}
