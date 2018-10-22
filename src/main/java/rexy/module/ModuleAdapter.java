package rexy.module;

import com.fasterxml.jackson.databind.JsonNode;
import rexy.config.model.Api;
import rexy.http.RexyRequest;
import rexy.http.RexyResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.Optional.empty;

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
	public Optional<RexyResponse> handleRequest(Api api, RexyRequest request) throws IOException {
		return empty();
	}
	
}