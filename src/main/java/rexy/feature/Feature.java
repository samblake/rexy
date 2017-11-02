package rexy.feature;

import com.sun.net.httpserver.HttpExchange;
import rexy.config.model.Api;

import java.io.IOException;
import java.util.Map;

/**
 * A feature defines configurable logic that can be applied to an endpoint. A feature may or may not
 * write a response. A feature must have a public no-args constructor.
 */
public interface Feature {
	
	/**
	 * Returns the name of the feature. This must match the name of the feature supplied in the
	 * {@link rexy.config.model.Config}.
	 *
	 * @return The feature's name
	 */
	String getName();
	
	/**
	 * Initialises the feature with the config supplied for the {@link rexy.config.model.Feature}.
	 *
	 * @param config The feature configuration
	 * @throws FeatureInitialisationException Thrown if the feature cannot be initialised
	 */
	void init(Map<String, Object> config) throws FeatureInitialisationException;
	
	/**
	 * Initialises in the feature for a specific @{@link Api API}. This will be called after the {@code init} method.
	 *
	 * @param api The API endpoint to initialise
	 * @throws FeatureInitialisationException Thrown if the feature cannot be initialised
	 */
	void initEndpoint(Api api) throws FeatureInitialisationException;
	
	/**
	 * Handlers a HTTP request against the context the API is registered against. If the feature
	 * writes the response then true should be returned meaning the request has been handled and no
	 * other features will be called.
	 *
	 * @param api      The API the request is for
	 * @param exchange The HTTP Exchange that is precessing the request
	 * @return True if the response has been written, false otherwise
	 *
	 * @throws IOException Thrown if the response could not be written
	 */
	boolean handleRequest(Api api, HttpExchange exchange) throws IOException;
}