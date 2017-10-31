package rexy.feature;

import com.sun.net.httpserver.HttpExchange;
import rexy.config.model.Api;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class FeatureAdapter implements Feature {
	
	private static final Pattern FEATURE_PATTERN = Pattern.compile("Feature");
	
	@Override
	public boolean enabledDefault() {
		return true;
	}
	
	@Override
	public String getName() {
		return FEATURE_PATTERN.matcher(getClass().getSimpleName()).replaceAll("").toLowerCase();
	}
	
	@Override
	public void init(Map<String, Object> config) throws FeatureInitialisationException {
	}
	
	@Override
	public void endpointCreation(Api api) throws FeatureInitialisationException {
	}
	
	@Override
	public boolean onRequest(Api api, HttpExchange exchange) throws IOException {
		return true;
	}
}