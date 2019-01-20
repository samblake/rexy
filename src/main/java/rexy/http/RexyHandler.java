package rexy.http;

import com.codepoetics.ambivalence.Either;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.Rexy;
import rexy.config.model.Api;
import rexy.http.request.RexyRequest;
import rexy.http.response.RexyResponse;
import rexy.module.RexyModule;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * A handler for {@link Api API} endpoints with the modules that should be applied to the API. The handler
 * will loop through the modules and apply them in order until one of them writes a response.
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
	
	/**
	 * Processes the request, creating a response. Each module processes the request in order until a response is
	 * created in which case no further modules will process the request. Once the response is created each module
	 * that processed the request will process the response in reverse order. Basically it goes up the chain until
	 * a response is created then it stops and goes down the chain.
	 *
	 * @param request The request to process
	 * @return An optional containing a {@link RexyResponse} if a response was created by one of the modules
	 *         or an empty optional if no response was created
	 * @throws IOException Thrown if the request could not be handled
	 */
	public Optional<RexyResponse> handle(RexyRequest request) throws IOException {
		logger.info("Handling request: " + request.getMethod() + ' ' + request.getUri());
		
		// TODO Create some sort of chain so we don't need to re-assign
		RexyRequest currentRequest = request;
		ListIterator<RexyModule> iterator = modules.listIterator();
		while (iterator.hasNext()) {
			Either<RexyRequest, RexyResponse> result = iterator.next().handleRequest(api, currentRequest);
			if (result.isRight()) {
				return processResponse(currentRequest, iterator, result.right().toOptional().get());
			}
			else {
				currentRequest = result.left().orElse(currentRequest);
			}
		}
		
		return empty();
	}
	
	private Optional<RexyResponse> processResponse(RexyRequest request,
			ListIterator<RexyModule> iterator, RexyResponse response) {
		
		RexyResponse currentResponse = response;
		while (iterator.hasPrevious()) {
			currentResponse = iterator.previous().processResponse(api, request, currentResponse);
		}
		
		return of(currentResponse);
	}
	
}