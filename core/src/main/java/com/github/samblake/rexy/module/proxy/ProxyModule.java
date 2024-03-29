package com.github.samblake.rexy.module.proxy;

import com.codepoetics.ambivalence.Either;
import com.github.samblake.rexy.config.model.Api;
import com.github.samblake.rexy.http.RexyHeader;
import com.github.samblake.rexy.http.request.RexyRequest;
import com.github.samblake.rexy.http.response.BasicRexyResponse;
import com.github.samblake.rexy.http.response.RexyResponse;
import com.github.samblake.rexy.module.ModuleAdapter;
import com.github.samblake.rexy.utils.Requests;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static com.codepoetics.ambivalence.Either.ofRight;
import static com.github.samblake.rexy.http.RexyHeader.HEADER_CONTENT_TYPE;
import static com.github.samblake.rexy.http.response.BasicRexyResponse.emptyResponse;
import static com.github.samblake.rexy.module.proxy.RequestFactory.createRequest;
import static com.github.samblake.rexy.utils.Requests.toHeaders;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.util.stream.Collectors.toList;

/**
 * <p>A module that proxies a request to another URL. This will always write a response so will always be the
 * last module in a chain.</p>
 *
 * <p>To configure a proxy an API is needs to be defined configuration. The {@code baseUrl} of the API corresponds
 * to the root path of the API on the Rexy server. The {@code proxy} value is the base URL that any request
 * to that path will be forwarded to. The URL before, and including, the {@code baseUrl} value is swapped for
 * the {@code proxy} value. This combined with the {@code endpoint} value forms the full URL that the request
 * will be proxied to.</p>
 *
 * <p>For example, in the following configuration, if Rexy was running on {@code localhost}, a request to
 * {@code http://localhost/metaweather-api} would be converted to {@code http://www.metaweather.com/api} and a
 * request to {@code http://localhost/metaweather-api/location/search/?query=london} would be proxied to
 * {@code http://www.metaweather.com/api/location/search/?query=london}.</p>
 *
 * <pre><code>{@code
 * "apis": [
 *   {
 *     "name": "metaweather",
 *     "baseUrl": "metaweather-api/",
 *     "contentType": "application/json",
 *     "proxy": "http://www.metaweather.com/api",
 *     "endpoints": [
 *     {
 *       "name": "location",
 *       "endpoint": "location/search/?query={query}",
 *     }
 *   }
 * ]
 * }</code></pre>
 */
public class ProxyModule extends ModuleAdapter {
	private static final Logger logger = LogManager.getLogger(ProxyModule.class);
	
	private static final int BAD_GATEWAY = 502;
	
	@Override
	public Either<RexyRequest, RexyResponse> handleRequest(Api api, RexyRequest request) throws IOException {
		logger.info("Proxying request for " + request.getUri());
		
		if (api.getProxy() == null) {
			logger.warn("Unable to fulfil request, no proxy defined for " + api.getName());
			ContentType contentType = ContentType.parse(getContentType(request, api));
			return ofRight(emptyResponse(BAD_GATEWAY, contentType.getMimeType(), toHeaders(api.getHeaders())));
		}
		else {
			HttpUriRequest proxyRequest = createRequest(api.getProxy(), request);
			// TODO don't create clients for every request, perhaps per API
			try (CloseableHttpClient client = HttpClients.createDefault()) {
				return ofRight(createResponse(client.execute(proxyRequest)));
			}
		}
	}
	
	private String getContentType(RexyRequest request, Api api) {
		return api.getContentType() != null ? api.getContentType() : Requests.getContentType(request);
	}
	
	private RexyResponse createResponse(CloseableHttpResponse response) throws IOException {
		byte[] body = getBody(response);
		int statusCode = response.getStatusLine().getStatusCode();
		
		List<RexyHeader> headers = Arrays.stream(response.getAllHeaders())
				.filter(h -> !isChunkedEncoding(h)) // We're always going to send a fixed length response
				.map(h -> new RexyHeader(h.getName(), h.getValue())).collect(toList());
		
		return new BasicRexyResponse(statusCode, headers, null, body);
	}
	
	private boolean isChunkedEncoding(Header header) {
		return header.getName().equals("Transfer-Encoding") && header.getValue().equals("chunked");
	}
	
	private byte[] getBody(CloseableHttpResponse response) throws IOException {
		Charset charset = getCharset(response);
		try (InputStream content = response.getEntity().getContent()) {
			try (Scanner scanner = new Scanner(content, charset.name()).useDelimiter("\\A")) {
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