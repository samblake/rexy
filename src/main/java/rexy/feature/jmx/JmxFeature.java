package rexy.feature.jmx;

import com.sun.net.httpserver.HttpExchange;
import org.apache.http.Header;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rexy.config.Api;
import rexy.config.Endpoint;
import rexy.feature.FeatureAdapter;
import rexy.feature.FeatureInitialisationException;

import javax.management.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class JmxFeature extends FeatureAdapter {
	private static final Logger logger = LoggerFactory.getLogger(JmxFeature.class);

	private JmxRegistry registry;

	@Override
	public void init(JsonNode jsonNode) throws FeatureInitialisationException {
		registry = JmxRegistry.getInstance();
	}

	@Override
	public void endpointCreation(Api api) throws FeatureInitialisationException {
		for (Endpoint endpoint : api.getEndpoints()) {
			try {
				registry.addEndpoint(api, endpoint);
			}
			catch (JMException e) {
				String message = "Could not create endpoint for " + api.getBaseUrl() + endpoint.getEndpoint();
				throw new FeatureInitialisationException(this, message, e);
			}
		}
	}

	@Override
	public boolean onRequest(Api api, HttpExchange exchange) {
		MockEndpoint endpoint = registry.getEndpoint(exchange.getRequestURI().getPath());
		if (endpoint != null) {
			if (endpoint.isIntercept()) {
				logger.info("Returning mock response for " + exchange.getRequestURI().getPath());

				try {
					sendResponse(exchange, api, endpoint);
				}
				catch (IOException e) {
					logger.error("Error sending response for " + exchange.getRequestURI().getPath(), e);
				}

				return false;
			}
		}
		else {
			logger.debug("No match for " + exchange.getRequestURI().getPath());
		}
		return true;
	}

	private void sendResponse(HttpExchange exchange, Api api, MockEndpoint endpoint) throws IOException {
		byte[] body = endpoint.getResponse() == null ? null : endpoint.getResponse().getBytes();
		int contentLength = body == null ? 0 : body.length;
		int httpStatus = endpoint.getHttpStatus();
		exchange.sendResponseHeaders(httpStatus, contentLength);

		List<String> contentType = exchange.getRequestHeaders().get("Content-Type");
		if (contentType != null) {
			for (String value : contentType) {
				exchange.getResponseHeaders().add("Content-Type", value);
			}
		}

		// TODO allow override fom endpoint
		for (Map.Entry<String, String> header : api.getHeaders().getHeaders()) {
            exchange.getResponseHeaders().add(header.getKey(), header.getValue());
        }

		OutputStream os = exchange.getResponseBody();
		os.write(body);
		os.close();
	}
}
