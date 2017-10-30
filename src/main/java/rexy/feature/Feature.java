package rexy.feature;

import com.sun.net.httpserver.HttpExchange;
import org.codehaus.jackson.JsonNode;
import rexy.config.Api;

import java.io.IOException;

public interface Feature {
	
	String getName();
	
	void init(JsonNode config) throws FeatureInitialisationException;
	
	void endpointCreation(Api api) throws FeatureInitialisationException;

	boolean onRequest(Api api, HttpExchange exchange);
}