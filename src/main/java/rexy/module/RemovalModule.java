package rexy.module;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.model.Api;
import rexy.http.RexyHeader;
import rexy.http.RexyRequest;
import rexy.http.response.RexyResponse;

import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.Arrays.stream;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.joining;
import static rexy.http.Method.OPTIONS;
import static rexy.http.RexyHeader.ACCESS_CONTROL_REQUEST_HEADERS;
import static rexy.http.RexyHeader.X_REQUESTED_WTH;
import static rexy.utils.Json.stringValue;

public class RemovalModule extends ModuleAdapter {
	private static final Logger logger = LogManager.getLogger(RemovalModule.class);
	
	private static final String CONFIG_HEADER_NAME = "headerName";
	
	private static final Pattern COMMA_SPLIT = Pattern.compile("\\s+,\\s+");
	
	private String headerName;
	
	@Override
	public void init(JsonNode config) {
		headerName = stringValue(config, CONFIG_HEADER_NAME, X_REQUESTED_WTH);
	}
	
	@Override
	public Optional<RexyResponse> handleRequest(Api api, RexyRequest request) {
		if (request.getMethod() == OPTIONS) {
			Optional<RexyHeader> requestHeaders = request.getHeaders().stream()
					.filter(header -> header.is(ACCESS_CONTROL_REQUEST_HEADERS)).findAny();
			
			if (requestHeaders.isPresent()) {
				removeFromAccessControlRequest(request, requestHeaders.get());
			}
		}
		else {
			boolean removed = request.getHeaders().removeIf(header -> header.is(headerName));
			if (removed) {
				logger.info("Removed " + headerName + " from request headers");
			}
		}
		
		return empty();
	}
	
	private void removeFromAccessControlRequest(RexyRequest request, RexyHeader requestHeaders) {
		String filteredHeader = stream(COMMA_SPLIT.split(requestHeaders.getValue(), -1))
				.filter(value -> !value.equalsIgnoreCase(headerName))
				.collect(joining(", "));
		
		request.getHeaders().removeIf(header -> header.is(ACCESS_CONTROL_REQUEST_HEADERS));
		
		if (filteredHeader.isEmpty()) {
			logger.info("Removed " + ACCESS_CONTROL_REQUEST_HEADERS);
		}
		else {
			request.getHeaders().add(new RexyHeader(ACCESS_CONTROL_REQUEST_HEADERS, filteredHeader));
			logger.info("Removed " + headerName + " from " + ACCESS_CONTROL_REQUEST_HEADERS);
		}
	}
	
}