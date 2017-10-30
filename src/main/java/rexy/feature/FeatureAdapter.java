package rexy.feature;

import com.sun.net.httpserver.HttpExchange;
import rexy.config.Api;

import java.io.IOException;

public abstract class FeatureAdapter implements Feature {
	
	@Override
	public String getName() {
		return getClass().getSimpleName().replace("Feature", "").toLowerCase();
	}
	
	@Override
	public void preInit() throws FeatureInitialisationException {
	}
	
	@Override
	public boolean endpointCreation(Api api) {
		return true;
	}
	
	@Override
	public void postInit() throws IOException {
	}
	
	@Override
	public boolean onRequest(Api api, HttpExchange exchange) {
		return true;
	}
}