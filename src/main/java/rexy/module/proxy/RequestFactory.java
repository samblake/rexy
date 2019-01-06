package rexy.module.proxy;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import rexy.http.Method;
import rexy.http.RexyHeader;
import rexy.http.request.RexyRequest;

import static rexy.http.Method.DELETE;
import static rexy.http.Method.GET;
import static rexy.http.Method.HEAD;
import static rexy.http.Method.OPTIONS;
import static rexy.http.Method.PATCH;
import static rexy.http.Method.POST;
import static rexy.http.Method.PUT;
import static rexy.http.Method.TRACE;

/**
 * Takes a {@link RexyRequest} and converts it into a {@link HttpUriRequest} that can be used to proxy
 * the request to another URL.
 */
public final class RequestFactory {
	
	private RequestFactory() {
	}
	
	/**
	 * Creates a request that forwards the request in the exchange to the base URL.
	 *
	 * @param baseUrl The URL to forward the request to
	 * @param request The exchange containing the request to forward
	 * @return The proxy request
	 */
	public static HttpUriRequest createRequest(String baseUrl, RexyRequest request) {
		String url = createUrl(baseUrl, request);
		HttpUriRequest proxyRequest = createRequest(request.getMethod(), url);
		
		request.getHeaders().stream().filter(RexyHeader::isProxyable)
				.forEach(header -> proxyRequest.addHeader(header.getName(), header.getValue()));
		
		return proxyRequest;
	}
	
	private static String createUrl(String baseUrl, RexyRequest request) {
		String url = baseUrl + request.getUri().replace(request.getContextPath(), "");
		return request.getQueryString() == null ? url : url + '?' + request.getQueryString();
	}
	
	/**
	 * Creates a request of a specific method (GET, POST, etc.) to the given URL.
	 *
	 * @param requestMethod The request method of the request
	 * @param url The URL of the request
	 * @return The created request
	 */
	public static HttpUriRequest createRequest(Method requestMethod, String url) {
		if (requestMethod == GET) {
			return new HttpGet(url);
		}
		if (requestMethod == POST) {
			return new HttpPost(url);
		}
		if (requestMethod == PUT) {
			return new HttpPut(url);
		}
		if (requestMethod == DELETE) {
			return new HttpDelete(url);
		}
		if (requestMethod == PATCH) {
			return new HttpPatch(url);
		}
		if (requestMethod == HEAD) {
			return new HttpHead(url);
		}
		if (requestMethod == OPTIONS) {
			return new HttpOptions(url);
		}
		if (requestMethod == TRACE) {
			return new HttpTrace(url);
		}
		throw new UnknownMethodException(requestMethod.name());
	}
	
	private static final class UnknownMethodException extends RuntimeException {
		
		private UnknownMethodException(String method) {
			super("Unknown HTTP method: " + method);
		}
		
	}
	
}