package rexy.feature.proxy;

import com.sun.net.httpserver.HttpExchange;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jackson.JsonNode;
import rexy.config.Api;
import rexy.feature.FeatureAdapter;
import rexy.feature.FeatureInitialisationException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map.Entry;

import static rexy.feature.proxy.RequestFactory.createRequest;

public class ProxyFeature extends FeatureAdapter {
	
	private CloseableHttpClient httpClient;
	
	@Override
	public void init(JsonNode config) throws FeatureInitialisationException {
		httpClient = HttpClients.createDefault();
	}
	
	@Override
	public boolean onRequest(Api api, HttpExchange exchange) {
		try {
			HttpUriRequest request = createRequest(exchange);
			CloseableHttpResponse response = httpClient.execute(request);
			writeResponse(exchange, response);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	private void writeResponse(HttpExchange exchange, CloseableHttpResponse response) throws IOException {
		int contentLength = (int)response.getEntity().getContentLength();
		int statusCode = response.getStatusLine().getStatusCode();
		exchange.sendResponseHeaders(statusCode, contentLength);
		
		for (Header header : response.getAllHeaders()) {
			exchange.getResponseHeaders().add(header.getName(), header.getValue());
		}
		
		OutputStream os = exchange.getResponseBody();
		byte[] body = new byte[contentLength];
		response.getEntity().getContent().read(body);
		os.write(body);
		os.close();
	}
	
}
