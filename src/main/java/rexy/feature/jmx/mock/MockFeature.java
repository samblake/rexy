package rexy.feature.jmx.mock;

import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.model.Api;
import rexy.feature.jmx.JmxFeature;
import rexy.feature.jmx.JmxRegistry;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import static java.nio.charset.Charset.defaultCharset;
import static rexy.http.Headers.HEADER_CONTENT_TYPE;

/**
 * <p>A feature that intercepts a request and optionally returns a mock response.</p>
 *
 * <p>To configure a delay an API is created in the configuration. Each endpoint has an an MBean associated with
 * it under the name {@code Rexy/[API name]/[Endpoint name]/mock}. The MBean has an <i>intercept</i> property which
 * is set to <i>false</i> by default. When it is set to <i>true</i> no mock response is returned and the feature
 * chain continues, when it is set to <i>true</i> a mock response is written and no further MBeans in the chain will
 * be called.</p>
 *
 * <p>The MBean has other properties: <i>content type</i>, <i>HTTP status</i> and <i>body</i>. These values
 * dictate what is returned as the mock response.</p>
 *
 * <p>For example, in the following configuration an MBean would be configured under
 * {@code Rexy/metaweather/location/mock}.</p>
 *
 * <p>{@code
 * "apis": [
 *   {
 *     "name": "metaweather",
 *     "baseUrl": "api/",
 *     "contentType": "application/json",
 *     "proxy": "http://www.metaweather.com/api",
 *     "endpoints": [{
 *       "name": "location",
 *       "endpoint": "/location/search/?query={query}",
 *       "responses": [{
 *           "name": "Successful",
 *           "httpStatus": 200,
 *           "body": {
 *             "title": "London",
 *             "location_type": "City",
 *             "woeid": 44418,
 *             "latt_long": "51.506321,-0.12714"
 *           }
 *         },
 *         {
 *           "name": "Invalid",
 *           "httpStatus": 401,
 *           "body": {
 *             "error": "invalid request"
 *           }
 *         }
 *       ]
 *     }]
 *   }
 * ]
 * }</p>
 *
 * <p>It is also possible to create presets that can be set to be returned. These can be seen in the above
 * example under the <i>responses</i> array. Each response will have bean an MBean associated with it under
 * {@code Rexy/metaweather/location/preset-[name]} where <i>name</i> is the name of the response. If no name
 * is given the index of the response in the array is used instead. The http status, headers and body to
 * return can be specified in the response. The <i>body</i> value can be any unstructured JSON.</p>
 *
 * <p>The feature has a single configuration value - <i>interceptOnSet</i>. If this property is set to true
 * then, when the set operator is called on an example response, the mock bean will have it's <i>intercept</i>
 * value set to <i>true</i>.</p>
 */
public class MockFeature extends JmxFeature<MockEndpoint> {
	private static final Logger logger = LogManager.getLogger(MockFeature.class);
	
	private static final String CONFIG_INTERCEPT_ON_SET = "interceptOnSet";
	
	@Override
	protected JmxRegistry<MockEndpoint> createRegistry(Map<String, Object> config) {
		return new MockRegistry(isInterceptOnSet(config));
	}
	
	private boolean isInterceptOnSet(Map<String, Object> config) {
		Object iosValue = config.get(CONFIG_INTERCEPT_ON_SET);
		return iosValue != null && Boolean.parseBoolean(iosValue.toString());
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
		
		List<String> contentType = exchange.getRequestHeaders().get(HEADER_CONTENT_TYPE);
		if (contentType != null) {
			for (String value : contentType) {
				exchange.getResponseHeaders().add(HEADER_CONTENT_TYPE, value);
			}
		}
		
		// TODO allow override from endpoint
		for (Map.Entry<String, String> header : api.getHeaders().entrySet()) {
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
