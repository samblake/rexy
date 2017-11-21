package rexy.module.proxy;

import com.sun.net.httpserver.HttpExchange;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;
import java.util.Map.Entry;

import static rexy.http.Headers.STRIP_HEADERS;

/**
 * Takes a {@link HttpExchange} and converts it into a {@link HttpUriRequest} that can be used to proxy
 * the request to another URL.
 */
public final class RequestFactory {
	
	private RequestFactory() {
	}
	
	/**
	 * Creates a request that forwards the request in the exchange to the base URL.
	 *
	 * @param baseUrl  The URL to forward the request to
	 * @param exchange The exchange containing the request to forward
	 * @return The proxy request
	 */
	public static HttpUriRequest createRequest(String baseUrl, HttpExchange exchange) {
		String url = createUrl(baseUrl, exchange);
		HttpUriRequest request = createRequest(exchange.getRequestMethod(), url);
		
		for (Entry<String, List<String>> header : exchange.getRequestHeaders().entrySet()) {
			for (String value : header.getValue()) {
				if (!STRIP_HEADERS.contains(header.getKey())) {
					request.addHeader(header.getKey(), value);
				}
			}
		}
		
		return request;
	}
	
	private static String createUrl(String baseUrl, HttpExchange exchange) {
		String url = baseUrl + exchange.getRequestURI().getPath().replace(exchange.getHttpContext().getPath(), "");
		return exchange.getRequestURI().getQuery() == null ? url : url + '?' + exchange.getRequestURI().getQuery();
	}
	
	/**
	 * Creates a request of a specific method (GET, POST, etc.) to the given URL.
	 *
	 * @param requestMethod The request method of the request
	 * @param url The URL of the request
	 * @return The created request
	 */
	public static HttpUriRequest createRequest(String requestMethod, String url) {
		if (requestMethod.equals(HttpGet.METHOD_NAME)) {
			return new HttpGet(url);
		}
		if (requestMethod.equals(HttpPost.METHOD_NAME)) {
			return new HttpPost(url);
		}
		if (requestMethod.equals(HttpPut.METHOD_NAME)) {
			return new HttpPut(url);
		}
		if (requestMethod.equals(HttpDelete.METHOD_NAME)) {
			return new HttpDelete(url);
		}
		if (requestMethod.equals(HttpPatch.METHOD_NAME)) {
			return new HttpPatch(url);
		}
		if (requestMethod.equals(HttpHead.METHOD_NAME)) {
			return new HttpHead(url);
		}
		if (requestMethod.equals(HttpOptions.METHOD_NAME)) {
			return new HttpOptions(url);
		}
		if (requestMethod.equals(HttpTrace.METHOD_NAME)) {
			return new HttpTrace(url);
		}
		throw new UnknownMethodException(requestMethod);
	}
	
	private static final class UnknownMethodException extends RuntimeException {
		
		private UnknownMethodException(String method) {
			super("Unknown http method: " + method);
		}
	}
}
