package rexy.module;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import rexy.config.model.Api;

import java.io.IOException;

/**
 * A module defines configurable logic that can be applied to an endpoint. A module may or may not
 * write a response. A module must have a public no-args constructor.
 */
public interface Module {
	
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
	 * @param config The module configuration
	 * @throws ModuleInitialisationException Thrown if the module cannot be initialised
	 */
	void init(JsonNode config) throws ModuleInitialisationException;
	
	/**
	 * Initialises in the module for a specific @{@link Api API}. This will be called after the {@code init} method.
	 *
	 * @param api The API endpoint to initialise
	 * @throws ModuleInitialisationException Thrown if the module cannot be initialised
	 */
	void initEndpoint(Api api) throws ModuleInitialisationException;
	
	/**
	 * Handlers a HTTP request against the context the API is registered against. If the module
	 * writes the response then true should be returned meaning the request has been handled and no
	 * other modules will be called.
	 *
	 * @param api      The API the request is for
	 * @param exchange The HTTP Exchange that is precessing the request
	 * @return True if the response has been written, false otherwise
	 *
	 * @throws IOException Thrown if the response could not be written
	 */
	boolean handleRequest(Api api, HttpExchange exchange) throws IOException;
}