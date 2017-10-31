package rexy.feature;

import com.sun.net.httpserver.HttpExchange;
import org.codehaus.jackson.JsonNode;
import rexy.config.Api;

import java.io.IOException;

public abstract class FeatureAdapter implements Feature {
	
	@Override
	public String getName() {
		return getClass().getSimpleName().replace("Feature", "").toLowerCase();
	}
	
	@Override
	public void init(JsonNode config) throws FeatureInitialisationException {
	}
	
	@Override
	public void endpointCreation(Api api) throws FeatureInitialisationException {
	}
	
	@Override
	public boolean onRequest(Api api, HttpExchange exchange) throws IOException {
		return true;
	}
}