package rexy.module;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.Rexy;
import rexy.config.model.Api;
import rexy.http.Method;
import rexy.http.RexyHeader;
import rexy.http.RexyRequest;
import rexy.http.response.RexyResponse;
import rexy.http.response.RexyResponseDelegate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.stream;
import static java.util.Optional.of;
import static java.util.stream.Collectors.joining;
import static rexy.http.Method.OPTIONS;
import static rexy.http.RexyHeader.ACCESS_CONTROL_ALLOW_HEADERS;
import static rexy.http.RexyHeader.ACCESS_CONTROL_ALLOW_METHODS;
import static rexy.http.RexyHeader.ACCESS_CONTROL_ALLOW_ORIGIN;
import static rexy.http.RexyHeader.ACCESS_CONTROL_REQUEST_HEADERS;
import static rexy.http.RexyHeader.X_REQUESTED_WTH;
import static rexy.http.RexyHeader.toMap;
import static rexy.http.response.BasicRexyResponse.emptyResponse;
import static rexy.utils.Json.stringValue;
import static rexy.utils.Streams.flattern;

/**
 * Generates a preflight response for OPTIONS requests. Adds/overrides the Access-Control-Allow-Headers header
 * to the response of all other requests.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Glossary/Preflight_request">MDN</a>
 */
public class CorsModule extends ModuleAdapter {
	private static final Logger logger = LogManager.getLogger(Rexy.class);
	
	private static final String ALLOW_ALL = "*";
	
	public static final String CONFIG_ALLOW_ORIGIN = "allowOrigin";
	public static final String CONFIG_HEADER_NAME = "headerName";
	public static final String CONFIG_HEADER_VALUE = "headerValue";
	
	private static final String METHOD_NAMES = stream(Method.values()).map(Enum::name).collect(joining(", "));
	
	private String allowOrigin;
	private String headerName;
	private String headerValue;
	
	@Override
	public void init(JsonNode config) {
		allowOrigin = stringValue(config, CONFIG_ALLOW_ORIGIN, ALLOW_ALL);
		headerName = stringValue(config, CONFIG_HEADER_NAME, X_REQUESTED_WTH);
		headerValue = stringValue(config, CONFIG_HEADER_VALUE, null);
	}
	
	@Override
	public Optional<RexyResponse> handleRequest(Api api, RexyRequest request) throws IOException {
		if (request.getMethod() == OPTIONS) {
			return createPreflightResponse(request);
		}
		
		return super.handleRequest(api, request);
	}
	
	private Optional<RexyResponse> createPreflightResponse(RexyRequest request) {
		logger.info("Generating preflight response");
		
		List<RexyHeader> headers = new ArrayList<>();
		headers.add(new RexyHeader(ACCESS_CONTROL_ALLOW_ORIGIN, allowOrigin));
		headers.add(new RexyHeader(ACCESS_CONTROL_ALLOW_METHODS, METHOD_NAMES));
		
		request.getHeaders().stream()
				.filter(header -> header.is(ACCESS_CONTROL_REQUEST_HEADERS)).findAny()
				.map(header -> new RexyHeader(ACCESS_CONTROL_ALLOW_HEADERS, header.getValue()))
				.ifPresent(headers::add);
		
		return of(emptyResponse(200, headers));
	}
	
	@Override
	public RexyResponse processResponse(Api api, RexyRequest request, RexyResponse response) {
		if (headerValue == null || headerMatches(request)) {
			logger.info("Adding header " + ACCESS_CONTROL_ALLOW_HEADERS + ": " + allowOrigin);
			return new HeaderOverrideResponse(response, new RexyHeader(ACCESS_CONTROL_ALLOW_ORIGIN, allowOrigin));
		}
		
		return response;
	}
	
	private boolean headerMatches(RexyRequest request) {
		return request.getHeaders().stream().anyMatch(header -> header.is(headerName, headerValue));
	}
	
	private static final class HeaderOverrideResponse extends RexyResponseDelegate {
		
		private final Map<String, Collection<RexyHeader>> headerOverrides;
		
		private HeaderOverrideResponse(RexyResponse delegate, RexyHeader... overrides) {
			super(delegate);
			headerOverrides = toMap(overrides);
		}
		
		@Override
		public Collection<RexyHeader> getHeaders() {
			List<RexyHeader> headers = new ArrayList<>();
			
			for (Map.Entry<String, Collection<RexyHeader>> entry : super.getHeaderMap().entrySet()) {
				if (!headerOverrides.containsKey(entry.getKey())) {
					headers.addAll(entry.getValue());
				}
			}
			
			headers.addAll(flattern(headerOverrides.values()));
			
			return headers;
		}
		
		@Override
		public Map<String, Collection<RexyHeader>> getHeaderMap() {
			return toMap(getHeaders());
		}
		
	}
	
}