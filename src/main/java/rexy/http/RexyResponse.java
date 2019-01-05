package rexy.http;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class RexyResponse {
	private static final String MIME_PLAINTEXT = "text/plain";
	private static final Charset CHARSET = StandardCharsets.UTF_8;

	private final int statusCode;
	private final String mimeType;
	private final List<RexyHeader> headers;
	private final byte[] body;
	
	public RexyResponse(int statusCode, List<RexyHeader> headers, String mimeType, byte[] body) {
		this.statusCode = statusCode;
		this.mimeType = mimeType;
		this.headers = headers;
		this.body = body;
	}
	
	public RexyResponse(int statusCode, Map<String, String> headers, String mimeType, byte[] body) {
		this(statusCode, fromMap(headers), mimeType, body);
	}
	
	private static List<RexyHeader> fromMap(Map<String, String> headers) {
		return headers.entrySet().stream().map(RexyHeader::fromEntry).collect(toList());
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	public List<RexyHeader> getHeaders() {
		return headers;
	}
	
	public byte[] getBody() {
		return body;
	}
	
	public static RexyResponse errorResponse(int statusCode, String message, Object... args) {
		return new RexyResponse(statusCode, emptyList(), MIME_PLAINTEXT, format(message, args).getBytes(CHARSET));
	}
	
}