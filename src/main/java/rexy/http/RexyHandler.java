package rexy.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import rexy.config.model.Api;
import rexy.module.Module;

import java.io.IOException;
import java.util.List;

/**
 * A {@link HttpHandler HTTP handler} for {@link Api API} endpoints with the modules that should be applied
 * to the API. The handler will loop through the modules and apply them in order until one of them writes a
 * response.
 */
public class RexyHandler implements HttpHandler {
	
	private final Api api;
	private final List<Module> modules;
	
	/**
	 * Creates a handler for an API endpoint.
	 *
	 * @param api     The API the endpoint is for
	 * @param modules The modules that should be applied to the API
	 */
	public RexyHandler(Api api, List<Module> modules) {
		this.api = api;
		this.modules = modules;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		for (Module module : modules) {
			if (module.handleRequest(api, exchange)) {
				return;
			}
		}
	}
	
}