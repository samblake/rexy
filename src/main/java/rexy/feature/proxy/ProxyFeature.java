package rexy.feature.proxy;

import com.sun.net.httpserver.HttpExchange;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rexy.config.Api;
import rexy.feature.FeatureAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static rexy.feature.proxy.RequestFactory.createRequest;

public class ProxyFeature extends FeatureAdapter {
	private static final Logger logger = LoggerFactory.getLogger(ProxyFeature.class);
	
	@Override
	public boolean onRequest(Api api, HttpExchange exchange) throws IOException {
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
		
		OutputStream os = exchange.getResponseBody();
		os.write(body);
		os.flush();
		
		exchange.close();
	}
	
	private byte[] getBody(CloseableHttpResponse response) throws IOException {
		InputStream content = response.getEntity().getContent();
		java.util.Scanner scanner = new java.util.Scanner(content).useDelimiter("\\A");
		String body = scanner.hasNext() ? scanner.next() : "";
		return body.getBytes(); // TODO parse encoding from headers
	}
	
}
