package rexy.http;

import com.sun.net.httpserver.HttpServer;
import rexy.config.Api;
import rexy.config.Config;
import rexy.feature.Feature;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class Server {
	
	private final HttpServer server;
	
	public Server(Config config, List<Feature> features) throws IOException {
		server = HttpServer.create(new InetSocketAddress(config.getPort()), 0);
		for (Api api : config.getApis()) {
			server.createContext(config.getBaseUrl() + api.getBaseUrl(), new RexyHandler(api, features));
		}
		server.setExecutor(null); // creates a default executor
	}
	
	public void start() throws IOException {
		server.start();
	}
}
