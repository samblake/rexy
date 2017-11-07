package rexy.feature.jmx.mock;

import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
 * <p>A feature that intercepts a request and optionally returns a mock response.</p>
 *
 * <p>To configure a delay an API is created in the configuration. Each endpoint has an an MBean associated with
 * it under the name {@code Rexy/[API name]/[Endpoint name]/mock}. The MBean has an <i>intercept</i> property which
 * is set to <i>false</i> by default. When it is set to <i>true</i> no mock response is returned and the feature
 * chain continues, when it is set to <i>true</i> a mock response is written and no further MBeans in the chain will
 * be called.</p>
 *
 * <p>The MBean has  other properties: <i>content type</i>, <i>HTTP status</i> and <i>body</i>. These values
 * dictate what is returned as the mock response.</p>
 *
 *
 *
 *
 *
 *
 * <p>For example, if the following configuration, if Rexy was running on localhost going to
 * http://localhost/api would be proxied to http://www.metaweather.com/api and going to
 * http://localhost/api/location/search/?query=london would be proxied to
 * http://www.metaweather.com/api/location/search/?query=london.</p>
 *
 * <p>{@code
 * "apis": [
 *   {
 *     "name": "metaweather",
 *     "baseUrl": "api/",
 *     "contentType": "application/json",
 *     "proxy": "http://www.metaweather.com/api",
 *     "endpoints": [
 *     {
 *       "name": "location",
 *       "endpoint": "location/search/?query={query}",
 *     }
 *   }
 * ]
 * }</p>
 */
public class MockFeature extends JmxFeature<MockEndpoint> {
	private static final Logger logger = LogManager.getLogger(MockFeature.class);
	
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
		byte[] body = endpoint.getBody() == null ? null : endpoint.getBody().getBytes(defaultCharset());
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
