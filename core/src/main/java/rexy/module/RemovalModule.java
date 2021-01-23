package rexy.module;

import com.codepoetics.ambivalence.Either;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.Rexy.RexyDetails;
import rexy.config.model.Api;
import rexy.http.RexyHeader;
import rexy.http.request.RexyRequest;
import rexy.http.request.RexyRequestDelegate;
import rexy.http.response.RexyResponse;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.codepoetics.ambivalence.Either.ofLeft;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static rexy.http.Method.OPTIONS;
import static rexy.http.RexyHeader.ACCESS_CONTROL_REQUEST_HEADERS;
import static rexy.http.RexyHeader.X_REQUESTED_WTH;
import static rexy.utils.Json.stringValue;

/**
 * A module that removes a header from requests.
 *
 * <pre><code>{@code
 * "removal": {
 *   "enabled": true,
 *   "headerName": "X-Requested-With"
 * }
 * }</code></pre>
 *
 * <table summary="Configuration details">
 *     <tr>
 *         <th>Name</th>
 *         <th>Description</th>
 *         <th>Type</th>
 *         <th>Default</th>
 *     </tr>
 *     <tr>
 *         <td>headerName</td>
 *         <td>The name of the header to removed.</td>
 *         <td>string</td>
 *         <td>X-Requested-With</td>
 *     </tr>
 * </table>
 */
public class RemovalModule extends ModuleAdapter {
	private static final Logger logger = LogManager.getLogger(RemovalModule.class);
	
	private static final String CONFIG_HEADER_NAME = "headerName";
	
	private static final Pattern COMMA_SPLIT = Pattern.compile("\\s+,\\s+");
	
	private String headerName;
	
	@Override
	public void init(RexyDetails rexyDetails, JsonNode config) {
		headerName = stringValue(config, CONFIG_HEADER_NAME, X_REQUESTED_WTH);
	}
	
	@Override
	public Either<RexyRequest, RexyResponse> handleRequest(Api api, RexyRequest request) {
		if (request.getMethod() == OPTIONS) {
			Optional<RexyHeader> requestHeaders = request.getHeaders().stream()
					.filter(header -> header.is(ACCESS_CONTROL_REQUEST_HEADERS)).findAny();
			
			if (requestHeaders.isPresent()) {
				return ofLeft(removeFromAccessControlRequest(request, requestHeaders.get()));
			}
		}
		else {
			logger.info("Removed " + headerName + " from request headers");
			return ofLeft(new RemovedHeaderRequest(request, ACCESS_CONTROL_REQUEST_HEADERS));
		}
		
		return ofLeft(request);
	}
	
	private RexyRequest removeFromAccessControlRequest(RexyRequest request, RexyHeader requestHeaders) {
		String filteredHeader = stream(COMMA_SPLIT.split(requestHeaders.getValue(), -1))
				.filter(value -> !value.equalsIgnoreCase(headerName))
				.collect(joining(", "));
		
		if (filteredHeader.isEmpty()) {
			logger.info("Removed " + ACCESS_CONTROL_REQUEST_HEADERS);
			return new RemovedHeaderRequest(request, ACCESS_CONTROL_REQUEST_HEADERS);
		}
		else {
			logger.info("Removed " + headerName + " from " + ACCESS_CONTROL_REQUEST_HEADERS);
			return new ModifiedHeaderRequest(request, ACCESS_CONTROL_REQUEST_HEADERS, filteredHeader);
		}
	}
	
	private static class ModifiedHeaderRequest extends RexyRequestDelegate {
		
		private final String name;
		private final String value;
		
		public ModifiedHeaderRequest(RexyRequest delegate, String name, String value) {
			super(delegate);
			this.name = name;
			this.value = value;
		}
		
		@Override
		public List<RexyHeader> getHeaders() {
			List<RexyHeader> headers = super.getHeaders().stream()
					.filter(header -> header.is(name))
					.collect(toList());
			
			headers.add(new RexyHeader(name, value));
			
			return headers;
		}
		
	}
	
	private static class RemovedHeaderRequest extends RexyRequestDelegate {
		
		private final String name;
		
		public RemovedHeaderRequest(RexyRequest delegate, String name) {
			super(delegate);
			this.name = name;
		}
		
		@Override
		public List<RexyHeader> getHeaders() {
			return super.getHeaders().stream()
					.filter(header -> header.is(name))
					.collect(toList());
		}
		
	}
	
	
}