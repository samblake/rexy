package rexy.feature.proxy;

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

import java.net.URI;
import java.util.List;
import java.util.Map.Entry;

public class RequestFactory {
	
	public static HttpUriRequest createRequest(HttpExchange exchange) {
		HttpUriRequest request = createRequest(exchange.getRequestMethod(), exchange.getRequestURI());
		
		for (Entry<String, List<String>> header : exchange.getRequestHeaders().entrySet()) {
			for (String value : header.getValue()) {
				request.addHeader(header.getKey(), value);
			}
		}
		
		return request;
	}
	
	public static HttpUriRequest createRequest(String requestMethod, URI requestURI) {
		if (requestMethod.equals(HttpGet.METHOD_NAME)) {
			return new HttpGet(requestURI);
		}
		if (requestMethod.equals(HttpPost.METHOD_NAME)) {
			return new HttpPost(requestURI);
		}
		if (requestMethod.equals(HttpPut.METHOD_NAME)) {
			return new HttpPut(requestURI);
		}
		if (requestMethod.equals(HttpDelete.METHOD_NAME)) {
			return new HttpDelete(requestURI);
		}
		if (requestMethod.equals(HttpPatch.METHOD_NAME)) {
			return new HttpPatch(requestURI);
		}
		if (requestMethod.equals(HttpHead.METHOD_NAME)) {
			return new HttpHead(requestURI);
		}
		if (requestMethod.equals(HttpOptions.METHOD_NAME)) {
			return new HttpOptions(requestURI);
		}
		if (requestMethod.equals(HttpTrace.METHOD_NAME)) {
			return new HttpTrace(requestURI);
		}
		throw new RuntimeException("Unknown http method: " + requestMethod);
	}
}
