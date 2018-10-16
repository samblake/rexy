package rexy.module;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import rexy.config.model.Api;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * A module implementation that performs no logic. The name is bassed on the class name with 'Module'
 * removed, converted to lowercase.
 */
public abstract class ModuleAdapter implements Module {
	
	private static final Pattern MODULE_PATTERN = Pattern.compile("Module");
	
	private final String name = MODULE_PATTERN.matcher(getClass().getSimpleName()).replaceAll("").toLowerCase();
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void init(JsonNode config) throws ModuleInitialisationException {
	}
	
	@Override
	public void initEndpoint(Api api) throws ModuleInitialisationException {
	}
	
	@Override
	public boolean handleRequest(Api api, HttpExchange exchange) throws IOException {
		return true;
	}
	
}