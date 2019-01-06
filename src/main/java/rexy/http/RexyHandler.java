package rexy.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.Rexy;
import rexy.config.model.Api;
import rexy.http.response.RexyResponse;
import rexy.module.RexyModule;
import rexy.module.proxy.ProxyModule;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * A handler for {@link Api API} endpoints with the modules that should be applied
 * to the API. The handler will loop through the modules and apply them in order until one of them writes a
 * response.
 */
public class RexyHandler {
	private static final Logger logger = LogManager.getLogger(Rexy.class);
	
	private final Api api;
	private final List<RexyModule> modules;
	
	/**
	 * Creates a handler for an API endpoint.
	 *
	 * @param api     The API the endpoint is for
	 * @param modules The modules that should be applied to the API
	 */
	public RexyHandler(Api api, List<RexyModule> modules) {
		this.api = api;
		this.modules = modules;
	}
	
	public Api getApi() {
		return api;
	}
	
	public Optional<RexyResponse> handle(RexyRequest request) throws IOException {
		logger.info("Handling request: " + request.getMethod() + ' ' + request.getUri());
		
		ListIterator<RexyModule> iterator = modules.listIterator();
		while (iterator.hasNext()) {
			Optional<RexyResponse> response = iterator.next().handleRequest(api, request);
			if (response.isPresent()) {
				return processResponse(request, iterator, response.get());
			}
		}
		
		return empty();
	}
	
	private Optional<RexyResponse> processResponse(RexyRequest request,
			ListIterator<RexyModule> iterator, RexyResponse response) {
		
		RexyResponse rexyResponse = response;
		while (iterator.hasPrevious()) {
			rexyResponse = iterator.previous().processResponse(api, request, rexyResponse);
		}
		
		return of(rexyResponse);
	}
	
	public Optional<RexyResponse> proxy(RexyRequest request) throws IOException {
		
		Optional<RexyModule> proxy = modules.stream()
				.filter(module -> module instanceof ProxyModule)
				.findAny();
		
		return proxy.isPresent() ? proxy.get().handleRequest(api, request) : empty();
	}
	
}