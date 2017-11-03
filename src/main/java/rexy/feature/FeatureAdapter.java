package rexy.feature;

import com.sun.net.httpserver.HttpExchange;
import rexy.config.model.Api;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A feature implementation that performs no logic. The name is bassed on the class name with 'Feature'
 * removed, converted to lowercase.
 */
public abstract class FeatureAdapter implements Feature {
	
	private static final Pattern FEATURE_PATTERN = Pattern.compile("Feature");
	
	private final String NAME = FEATURE_PATTERN.matcher(getClass().getSimpleName()).replaceAll("").toLowerCase();
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public void init(Map<String, Object> config) throws FeatureInitialisationException {
	}
	
	@Override
	public void initEndpoint(Api api) throws FeatureInitialisationException {
	}
	
	@Override
	public boolean handleRequest(Api api, HttpExchange exchange) throws IOException {
		return true;
	}
}