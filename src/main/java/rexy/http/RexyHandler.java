package rexy.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import rexy.config.model.Api;
import rexy.feature.Feature;

import java.io.IOException;
import java.util.List;

/**
 * A {@link HttpHandler HTTP handler} for {@link Api API} endpoints with the features that should be applied
 * to the API. The handler will loop through the features and apply them in order until one of them writes a
 * response.
 */
public class RexyHandler implements HttpHandler {
	
	private final Api api;
	private final List<Feature> features;
	
	/**
	 * Creates a handler for an API endpoint.
	 *
	 * @param api      The API the endpoint is for
	 * @param features The features that should be applied to the API
	 */
	public RexyHandler(Api api, List<Feature> features) {
		this.api = api;
		this.features = features;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		for (Feature feature : features) {
			if (feature.handleRequest(api, exchange)) {
				return;
			}
		}
	}
}