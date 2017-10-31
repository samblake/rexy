package rexy.feature;

import com.sun.net.httpserver.HttpExchange;
import rexy.config.Api;

import java.io.IOException;
import java.util.Map;

public interface Feature {
	
	boolean enabledDefault();
	
	String getName();
	
	void init(Map<String, Object> config) throws FeatureInitialisationException;
	
	void endpointCreation(Api api) throws FeatureInitialisationException;
	
	boolean onRequest(Api api, HttpExchange exchange) throws IOException;
}