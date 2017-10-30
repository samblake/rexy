package rexy.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import rexy.config.Api;
import rexy.feature.Feature;

import java.io.IOException;
import java.util.List;

public class RexyHandler implements HttpHandler {
	
	private final Api api;
	private final List<Feature> features;
	
	public RexyHandler(Api api, List<Feature> features) {
		this.api = api;
		this.features = features;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		for (Feature feature : features) {
			if (!feature.onRequest(api, exchange)) {
				break;
			}
		}
	}
}