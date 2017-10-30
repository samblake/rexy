package rexy.feature;

import com.sun.net.httpserver.HttpExchange;
import rexy.config.Api;

import java.io.IOException;

public interface Feature {
	
	String getName();
	
	void preInit() throws FeatureInitialisationException;
	
	boolean endpointCreation(Api api);
	
	void postInit() throws IOException;
	
	boolean onRequest(Api api, HttpExchange exchange);
}