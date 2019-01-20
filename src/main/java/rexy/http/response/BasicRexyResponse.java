package rexy.http.response;

import rexy.http.RexyHeader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.nio.charset.Charset.defaultCharset;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static rexy.utils.Streams.flattern;

/**
 * The standard implementation of a {@link RexyResponse}.
 */
public class BasicRexyResponse implements RexyResponse {
	private static final String MIME_PLAINTEXT = "text/plain";
	
	private static final byte[] EMPTY_BODY = new byte[0];
	
	private final int statusCode;
	private final String mimeType;
	private final Map<String, Collection<RexyHeader>> headers;
	private final byte[] body;
	
	/**
	 * Creates a new resposne from a list of headers. The body may be null in which case an empty body will be
	 * returned.
	 */
	public BasicRexyResponse(int statusCode, List<RexyHeader> headers, String mimeType, byte[] body) {
		this.statusCode = statusCode;
		this.mimeType = mimeType;
		this.headers = RexyHeader.toMap(headers);
		this.body = body == null ? EMPTY_BODY : body.clone();
	}
	
	/**
  	 * Creates a new resposne from a map of headers. The body may be null in which case an empty bodu will be
	 * returned.
	 */
	public BasicRexyResponse(int statusCode, Map<String, String> headers, String mimeType, byte[] body) {
		this(statusCode, fromMap(headers), mimeType, body);
	}
	
	private static List<RexyHeader> fromMap(Map<String, String> headers) {
		return headers.entrySet().stream().map(RexyHeader::fromEntry).collect(toList());
	}
	
	@Override
	public int getStatusCode() {
		return statusCode;
	}
	
	@Override
	public String getMimeType() {
		return mimeType;
	}
	
	@Override
	public Collection<RexyHeader> getHeaders() {
		return flattern(headers.values());
	}
	
	@Override
	public InputStream getBody() {
		return new ByteArrayInputStream(body);
	}

	@Override
	public int getResponseLength() {
		return body.length;
	}
	
	@Override
	public Map<String, Collection<RexyHeader>> getHeaderMap() {
		return headers;
	}
	
	/**
	 * A factory method to create a plaintext error response. The resonse will be created with no headers.
	 *
	 * @param statusCode The response status code
	 * @param message The message that should be returned as the response body in {@link String#format} style
	 * @param args The values that will be subsituted on the message
	 * @return The response
	 */
	public static RexyResponse errorResponse(int statusCode, String message, Object... args) {
		return new BasicRexyResponse(statusCode, emptyList(), MIME_PLAINTEXT, format(message, args).getBytes(defaultCharset()));
	}
	
	/**
	 * A factory method to create an empty, plaintext response.
	 *
	 * @param statusCode The response status code
	 * @param headers The headers to set on the response.
	 * @return The response
	 */
	public static RexyResponse emptyResponse(int statusCode, List<RexyHeader> headers) {
		return new BasicRexyResponse(statusCode, headers, MIME_PLAINTEXT, EMPTY_BODY);
	}
	
}