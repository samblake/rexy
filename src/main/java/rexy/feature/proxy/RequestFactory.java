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

import java.util.List;
import java.util.Map.Entry;

public class RequestFactory {
	
	public static HttpUriRequest createRequest(String baseUrl, HttpExchange exchange) {
		String url = createUrl(baseUrl, exchange);
		HttpUriRequest request = createRequest(exchange.getRequestMethod(), url);
		
		for (Entry<String, List<String>> header : exchange.getRequestHeaders().entrySet()) {
			for (String value : header.getValue()) {
				if (!header.getKey().equals("Host")) {
					request.addHeader(header.getKey(), value);
				}
			}
		}
		
		return request;
	}
	
	private static String createUrl(String baseUrl, HttpExchange exchange) {
		String url = baseUrl + exchange.getRequestURI().getPath().replace(exchange.getHttpContext().getPath(), "/");
		return exchange.getRequestURI().getQuery() == null ? url : url + "?" + exchange.getRequestURI().getQuery();
	}
	
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
		throw new RuntimeException("Unknown http method: " + requestMethod);
	}
}
