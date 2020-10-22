package rexy.module;

import com.codepoetics.ambivalence.Either;
import com.fasterxml.jackson.databind.JsonNode;
import rexy.Rexy.RexyDetails;
import rexy.config.model.Api;
import rexy.http.request.RexyRequest;
import rexy.http.response.BasicRexyResponse;
import rexy.http.response.RexyResponse;

import java.io.IOException;

/**
 * A module defines configurable logic that can be applied to an endpoint. A module may or may not
 * write a response. A module must have a public no-args constructor.
 */
public interface RexyModule {
	
	/**
	 * Returns the name of the module. This must match the name of the module supplied in the
	 * {@link rexy.config.model.Config}.
	 *
	 * @return The module's name
	 */
	String getName();
	
	/**
	 * Initialises the module with the config supplied for the module.
	 *
	 * @param rexyDetails Detail of the Rexy server itself
	 * @param config The module configuration
	 * @throws ModuleInitialisationException Thrown if the module cannot be initialised
	 */
	void init(RexyDetails rexyDetails, JsonNode config) throws ModuleInitialisationException;
	
	/**
	 * Initialises in the module for a specific @{@link Api API}. This will be called after the {@code init} method.
	 *
	 * @param api The API endpoint to initialise
	 * @throws ModuleInitialisationException Thrown if the module cannot be initialised
	 */
	void initEndpoint(Api api) throws ModuleInitialisationException;
	
	/**
	 * Handles a HTTP request against the context the API is registered against. If the request is processed
	 * and the response should be written then one should be returned. If this is the case no other modules will
	 * be called.
	 *
	 * @param api     The API the request is for
	 * @param request The request to process
	 * @return        The response if one has been created, the request otherwise
	 */
	Either<RexyRequest, RexyResponse> handleRequest(Api api, RexyRequest request) throws IOException;
	
	/**
	 * Processes a {@link BasicRexyResponse} request against the context the API is registered against.
	 *
	 * @param api      The API the request is for
	 * @param request  The request that created the response
	 * @param response The response to preocess
	 * @return         The processed response
	 */
	RexyResponse processResponse(Api api, RexyRequest request, RexyResponse response);
	
}