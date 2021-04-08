package com.github.samblake.rexy.module.proxy;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.*;
import com.github.samblake.rexy.http.Method;
import com.github.samblake.rexy.http.RexyHeader;
import com.github.samblake.rexy.http.request.RexyRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.util.List;

import static com.github.samblake.rexy.http.Method.*;
import static com.github.samblake.rexy.http.RexyHeader.HEADER_CONTENT_TYPE;

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
		
		if (proxyRequest instanceof HttpEntityEnclosingRequest && request.getBody() != null) {
			ContentType contentType = findContentType(request.getHeaders());
			HttpEntity entity = new StringEntity(request.getBody(), contentType);
			((HttpEntityEnclosingRequest)proxyRequest).setEntity(entity);
		}
		
		return proxyRequest;
	}
	
	private static String createUrl(String baseUrl, RexyRequest request) {
		String url = baseUrl + request.getUri().replace(request.getContextPath(), "");
		return request.getQueryString() == null ? url : url + '?' + request.getQueryString();
	}
	
	private static ContentType findContentType(List<RexyHeader> headers) {
		return headers.stream()
				.filter(h -> h.getName().equalsIgnoreCase(HEADER_CONTENT_TYPE))
				.map(h -> ContentType.parse(h.getValue()))
				.findAny().orElse(null);
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