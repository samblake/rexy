package rexy.http;

import rexy.config.model.Api;
import rexy.module.Module;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

/**
 * A handler for {@link Api API} endpoints with the modules that should be applied
 * to the API. The handler will loop through the modules and apply them in order until one of them writes a
 * response.
 */
public class RexyHandler {
	
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
	
	public Optional<RexyResponse> handle(RexyRequest request) throws IOException {
		for (Module module : modules) {
			Optional<RexyResponse> response = module.handleRequest(api, request);
			if (response.isPresent()) {
				return response;
			}
		}
		return empty();
	}
	
}