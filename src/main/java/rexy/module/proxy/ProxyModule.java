package rexy.module.proxy;

import com.sun.net.httpserver.HttpExchange;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.model.Api;
import rexy.module.ModuleAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static rexy.http.Headers.HEADER_CONTENT_TYPE;
import static rexy.module.proxy.RequestFactory.createRequest;

/**
 * <p>A module that proxies a request to another URL. This will always write a response so will always be the
 * last module in a chain.</p>
 *
 * <p>To configure a proxy an API is created in the configuration. The {@code baseUrl} of the API corresponds
 * to the context root of the API on the Rexy server. The {@code proxy} value is the base URL that any request
 * to that context root will be forwarded to.</p>
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
public class ProxyModule extends ModuleAdapter {
	private static final Logger logger = LogManager.getLogger(ProxyModule.class);
	
	@Override
	public boolean handleRequest(Api api, HttpExchange exchange) throws IOException {
		logger.info("Proxying request for " + exchange.getRequestURI().getPath());
		
		HttpUriRequest request = createRequest(api.getProxy(), exchange);
		// TODO don't create client for every request, perhaps per API
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			writeResponse(exchange, client.execute(request));
		}
		
		return true;
	}
	
	private void writeResponse(HttpExchange exchange, CloseableHttpResponse response) throws IOException {
		byte[] body = getBody(response);
		int contentLength = body.length;
		
		int statusCode = response.getStatusLine().getStatusCode();
		exchange.sendResponseHeaders(statusCode, contentLength);
		
		for (Header header : response.getAllHeaders()) {
			exchange.getResponseHeaders().add(header.getName(), header.getValue());
		}
		
		try (OutputStream os = exchange.getResponseBody()) {
			os.write(body);
		}
		
		exchange.close();
	}
	
	private byte[] getBody(CloseableHttpResponse response) throws IOException {
		Charset charset = getCharset(response);
		try (InputStream content = response.getEntity().getContent()) {
			try (Scanner scanner = new java.util.Scanner(content, charset.name()).useDelimiter("\\A")) {
				String body = scanner.hasNext() ? scanner.next() : "";
				return body.getBytes(charset);
			}
		}
	}
	
	private Charset getCharset(CloseableHttpResponse response) {
		Header[] contentType = response.getHeaders(HEADER_CONTENT_TYPE);
		for (Header header : contentType) {
			Charset charset = ContentType.parse(header.getValue()).getCharset();
			if (charset != null) {
				return charset;
			}
		}
		return ISO_8859_1;
	}
}