package rexy.feature.jmx.mock;

import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rexy.config.model.Api;
import rexy.config.model.Headers;
import rexy.feature.jmx.JmxFeature;
import rexy.feature.jmx.JmxRegistry;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import static java.nio.charset.Charset.defaultCharset;

/**
 * A feature that registers an MBean with every API endpoint. When the MBean is enabled it will intercept
 * the request and return a mocked response. Preset responses can be set.
 */
public class MockFeature extends JmxFeature<MockEndpoint> {
	private static final Logger logger = LoggerFactory.getLogger(MockFeature.class);
	
	@Override
	protected JmxRegistry<MockEndpoint> getRegistry() {
		return MockRegistry.getInstance();
	}
	
	@Override
	protected boolean handleRequest(Api api, HttpExchange exchange, MockEndpoint mBean) {
		if (mBean.isIntercept()) {
			logger.info("Returning mock response for " + exchange.getRequestURI().getPath());
			
			try {
				sendResponse(exchange, api, mBean);
			}
			catch (IOException e) {
				logger.error("Error sending response for " + exchange.getRequestURI().getPath(), e);
			}
			
			return true;
		}
		return false;
	}
	
	private void sendResponse(HttpExchange exchange, Api api, MockEndpoint endpoint) throws IOException {
		byte[] body = endpoint.getResponse() == null ? null : endpoint.getResponse().getBytes(defaultCharset());
		int contentLength = body == null ? 0 : body.length;
		int httpStatus = endpoint.getHttpStatus();
		exchange.sendResponseHeaders(httpStatus, contentLength);
		
		List<String> contentType = exchange.getRequestHeaders().get("Content-Type");
		if (contentType != null) {
			for (String value : contentType) {
				exchange.getResponseHeaders().add("Content-Type", value);
			}
		}
		
		// TODO allow override from endpoint
		Headers headers = api.getHeaders();
		for (Map.Entry<String, String> header : headers.getHeaders()) {
			exchange.getResponseHeaders().add(header.getKey(), header.getValue());
		}
		
		if (body != null) {
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(body);
			}
		}
		
		exchange.close();
	}
}
