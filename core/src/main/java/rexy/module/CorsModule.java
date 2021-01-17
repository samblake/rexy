package rexy.module;

import com.codepoetics.ambivalence.Either;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.Rexy;
import rexy.Rexy.RexyDetails;
import rexy.config.model.Api;
import rexy.http.Method;
import rexy.http.RexyHeader;
import rexy.http.request.RexyRequest;
import rexy.http.response.RexyResponse;
import rexy.http.response.RexyResponseDelegate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.codepoetics.ambivalence.Either.ofRight;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static rexy.http.Method.OPTIONS;
import static rexy.http.RexyHeader.*;
import static rexy.http.response.BasicRexyResponse.emptyResponse;
import static rexy.utils.Json.stringValue;
import static rexy.utils.Streams.flattern;

/**
 * <p>A module that generates a preflight response for OPTIONS requests and adds/overrides the
 * {@code Access-Control-Allow-Headers} header to the response of all other requests The module
 * can be triggered conditionally based on a specified header value. The configuration is:</p>
 *
 * <pre>{@code
 *   "cors": {
 *     "enabled": true,
 *     "allowOrigin": "*",
 *     "headerName": "X-Requested-With",
 *     "headerValue": "Wexy"
 *   }
 * }</pre>
 *
 * <table summary="Configuration details">
 *     <tr>
 *         <th>Name</th>
 *         <th>Description</th>
 *         <th>Type</th>
 *         <th>Default</th>
 *     </tr>
 *     <tr>
 *         <td>allowOrigin</td>
 *         <td>The allowOrigin header value.</td>
 *         <td>string</td>
 *         <td>*</td>
 *     </tr>
 *     <tr>
 *         <td>headerName</td>
 *         <td>The name of the header that should be used to conditionally trigger the module.</td>
 *         <td>string</td>
 *         <td>X-Requested-With</td>
 *     </tr>
 *     <tr>
 *         <td>headerValue</td>
 *         <td>The value of the header that should be used to conditionally trigger the module. If no value
 *         is set then the module will trigger for all requests.</td>
 *         <td>string</td>
 *         <td><i>null</i></td>
 *     </tr>
 * </table>
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
	public void init(RexyDetails rexyDetails, JsonNode config) {
		allowOrigin = stringValue(config, CONFIG_ALLOW_ORIGIN, ALLOW_ALL);
		headerName = stringValue(config, CONFIG_HEADER_NAME, X_REQUESTED_WTH);
		headerValue = stringValue(config, CONFIG_HEADER_VALUE);
	}
	
	@Override
	public Either<RexyRequest, RexyResponse> handleRequest(Api api, RexyRequest request) throws IOException {
		if (request.getMethod() == OPTIONS) {
			return ofRight(createPreflightResponse(request));
		}
		
		return super.handleRequest(api, request);
	}
	
	private RexyResponse createPreflightResponse(RexyRequest request) {
		logger.info("Generating preflight response");
		
		List<RexyHeader> headers = new ArrayList<>();
		headers.add(new RexyHeader(ACCESS_CONTROL_ALLOW_ORIGIN, allowOrigin));
		headers.add(new RexyHeader(ACCESS_CONTROL_ALLOW_METHODS, METHOD_NAMES));
		
		request.getHeaders().stream()
				.filter(header -> header.is(ACCESS_CONTROL_REQUEST_HEADERS)).findAny()
				.map(header -> new RexyHeader(ACCESS_CONTROL_ALLOW_HEADERS, header.getValue()))
				.ifPresent(headers::add);
		
		return emptyResponse(200, headers);
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