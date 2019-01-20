package rexy.http.response;

import rexy.http.RexyHeader;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * Models HTTP responses within Rexy.
 */
public interface RexyResponse {
	
	/**
	 * The HTTP status code.
	 */
	int getStatusCode();
	
	/**
	 * The response mine type.
	 */
	String getMimeType();
	
	/**
	 * Non-canonicalised response headers.
	 */
	Collection<RexyHeader> getHeaders();
	
	/**
	 * The response body (this may have no content).
	 */
	InputStream getBody();
	
	/**
	 * The length of the body in bytes.
	 */
	int getResponseLength();
	
	/**
	 * A map from canonicalised response header name to collection of headers.
	 */
	Map<String, Collection<RexyHeader>> getHeaderMap();
	
}