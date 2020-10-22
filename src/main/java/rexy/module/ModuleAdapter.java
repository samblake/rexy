package rexy.module;

import com.codepoetics.ambivalence.Either;
import com.fasterxml.jackson.databind.JsonNode;
import rexy.Rexy.RexyDetails;
import rexy.config.model.Api;
import rexy.http.request.RexyRequest;
import rexy.http.response.RexyResponse;

import java.io.IOException;
import java.util.regex.Pattern;

import static com.codepoetics.ambivalence.Either.ofLeft;

/**
 * A module implementation that performs no logic. The name is bassed on the class name with 'RexyModule'
 * removed, converted to lowercase.
 */
public abstract class ModuleAdapter implements RexyModule {
	
	private static final Pattern MODULE_PATTERN = Pattern.compile("Module");
	
	private final String name = MODULE_PATTERN.matcher(getClass().getSimpleName()).replaceAll("").toLowerCase();
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void init(RexyDetails rexyDetails, JsonNode config) throws ModuleInitialisationException {
	}
	
	@Override
	public void initEndpoint(Api api) throws ModuleInitialisationException {
	}
	
	@Override
	public Either<RexyRequest, RexyResponse> handleRequest(Api api, RexyRequest request) throws IOException {
		return ofLeft(request);
	}
	
	@Override
	public RexyResponse processResponse(Api api, RexyRequest request, RexyResponse response) {
		return response;
	}
	
}