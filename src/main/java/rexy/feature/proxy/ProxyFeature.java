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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rexy.config.Api;
import rexy.feature.FeatureAdapter;
import rexy.feature.FeatureInitialisationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map.Entry;

import static rexy.feature.proxy.RequestFactory.createRequest;

public class ProxyFeature extends FeatureAdapter {
	private static final Logger logger = LoggerFactory.getLogger(ProxyFeature.class);
	
	@Override
	public boolean onRequest(Api api, HttpExchange exchange) {
		try {
			logger.info("Proxying request for " + exchange.getRequestURI().getPath());

			HttpUriRequest request = createRequest(api.getProxy(), exchange);
			// FIXME don't create client for every request, perhaps per API
			CloseableHttpResponse response = HttpClients.createDefault().execute(request);
 			writeResponse(exchange, response);
		}
		catch (IOException e) {
			e.printStackTrace();
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

		logger.info("Writing response for " + exchange.getRequestURI().getPath());
		logger.info(new String(body));

		OutputStream os = exchange.getResponseBody();
		os.write(body);
		os.flush();

		exchange.close();
	}

	private byte[] getBody(CloseableHttpResponse response) throws IOException {
		InputStream content = response.getEntity().getContent();
		java.util.Scanner s = new java.util.Scanner(content).useDelimiter("\\A");
		String body = s.hasNext() ? s.next() : "";
		return body.getBytes(); // TODO parse encoding from headers
	}

}
