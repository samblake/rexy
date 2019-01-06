package rexy.module.jmx.mock;

import com.codepoetics.ambivalence.Either;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.model.Api;
import rexy.http.RexyHeader;
import rexy.http.request.RexyRequest;
import rexy.http.response.BasicRexyResponse;
import rexy.http.response.RexyResponse;
import rexy.module.ModuleInitialisationException;
import rexy.module.jmx.JmxModule;
import rexy.module.jmx.JmxRegistry;

import java.io.IOException;

import static com.codepoetics.ambivalence.Either.ofLeft;
import static com.codepoetics.ambivalence.Either.ofRight;
import static java.nio.charset.Charset.defaultCharset;
import static rexy.http.RexyHeader.HEADER_CONTENT_TYPE;
import static rexy.utils.Json.booleanValue;
import static rexy.utils.Json.prettyPrint;

/**
 * <p>A module that intercepts a request and optionally returns a mock response.</p>
 *
 * <p>To configure a delay an API is created in the configuration. Each endpoint has an an MBean associated with
 * it under the name {@code Rexy/[API name]/[Endpoint name]/mock}. The MBean has an <i>intercept</i> property which
 * is set to <i>false</i> by default. When it is set to <i>true</i> no mock response is returned and the module
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
 * <p>The module has a true configuration values - <i>interceptOnSet</i> and <i>prettyPrint</i>. If
 * interceptOnSet is set to true, when the set operator is called on an example response, the mock bean will
 * have it's <i>intercept</i> value set to <i>true</i>. If prettyPrint is set to true then mocked responses
 * will be pretty printed.</p>
 */
public class MockModule extends JmxModule<MockEndpoint> {
	private static final Logger logger = LogManager.getLogger(MockModule.class);
	
	private static final String CONFIG_INTERCEPT_ON_SET = "interceptOnSet";
	private static final String CONFIG_PRETTY_PRINT = "prettyPrint";
	
	private boolean prettyPrint;
	
	@Override
	public void init(JsonNode config) throws ModuleInitialisationException {
		super.init(config);
		prettyPrint = booleanValue(config, CONFIG_PRETTY_PRINT);
	}
	
	@Override
	protected JmxRegistry<MockEndpoint> createRegistry(JsonNode config) {
		return new MockRegistry(booleanValue(config, CONFIG_INTERCEPT_ON_SET));
	}
	
	@Override
	protected Either<RexyRequest, RexyResponse> handleRequest(Api api, RexyRequest request, MockEndpoint mBean) {
		if (mBean.isIntercept()) {
			logger.info("Returning mock response for " + request.getUri());
			
			try {
				return ofRight(createResponse(request, api, mBean));
			}
			catch (IOException e) {
				logger.error("Error sending response for " + request.getUri(), e);
			}
		}
		
		return ofLeft(request);
	}
	
	private RexyResponse createResponse(RexyRequest request, Api api, MockEndpoint endpoint) throws IOException {
		byte[] body = getBody(endpoint);
		ContentType contentType = ContentType.parse(getContentType(request, api, endpoint));
		return new BasicRexyResponse(endpoint.getHttpStatus(), api.getHeaders(), contentType.getMimeType(), body);
	}
	
	private byte[] getBody(MockEndpoint endpoint) {
		if (endpoint.getBody() == null) {
			return null;
		}
		
		String body = prettyPrint ? prettyPrint(endpoint.getBody()) : endpoint.getBody();
		
		// TODO Check for charset in headers
		return body.getBytes(defaultCharset());
	}
	
	private String getContentType(RexyRequest request, Api api, MockEndpoint endpoint) {
		String contentType = endpoint.getContentType() == null ? api.getContentType() : endpoint.getContentType();
		return contentType != null ? contentType : request.getHeaders()
				.stream().filter(h -> h.getName().equalsIgnoreCase(HEADER_CONTENT_TYPE))
				.findFirst().map(RexyHeader::getValue).orElse(null);
	}
	
}