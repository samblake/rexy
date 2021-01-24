package rexy.http;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;
import static rexy.utils.Streams.collectionMerger;

/**
 * Models HTTP headers within Rexy.
 */
public class RexyHeader {
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	
	public static final String HEADER_HOST = "Host";
	public static final String HEADER_CONNECTION = "Connection";
	public static final String HEADER_TE = "TE";
	public static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding";
	public static final String HEADER_KEEP_ALIVE = "Keep-Alive";
	public static final String HEADER_PROXY_AUTHORIZATION = "Proxy-Authorization";
	public static final String HEADER_PROXY_AUTHENTICATION = "Proxy-Authentication";
	public static final String HEADER_TRAILER = "Trailer";
	public static final String HEADER_UPGRADE = "Upgrade";
	public static final String HEADER_CONTENT_LENGTH = "Content-length";
	
	public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
	public static final String ACCESS_CONTROL_REQUEST_METHODS = "Access-Control-Request-Methods";
	public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	
	public static final String X_REQUESTED_WTH = "X-Requested-With";
	
	private static final Set<String> STRIP_HEADERS = unmodifiableSet(of(
			HEADER_HOST,
			HEADER_CONNECTION,
			HEADER_TE,
			HEADER_TRANSFER_ENCODING,
			HEADER_KEEP_ALIVE,
			HEADER_PROXY_AUTHORIZATION,
			HEADER_PROXY_AUTHENTICATION,
			HEADER_TRAILER,
			HEADER_UPGRADE,
			HEADER_CONTENT_LENGTH
	).map(String::toLowerCase).collect(toSet()));
	
	private final String name;
	private final String value;
	
	/**
	 * Create a new header. A single header can only have a single value, if multiple values are required
	 * then multiple instances should be created.
	 *
	 * @param name The header name
	 * @param value The header value
	 */
	public RexyHeader(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	/**
	 * A factory method to create a header from a {@code Map#Entry}. The key is used as the header name and the
	 * value as the header value.
	 */
	public static RexyHeader fromEntry(Entry<String, String> entry) {
		return new RexyHeader(entry.getKey(), entry.getValue());
	}
	
	/**
	 * Some headers should be removed when proxying a request. This method returns true for a header
	 * that should be removed.
	 */
	public boolean isProxyable() {
		return !STRIP_HEADERS.contains(name.toLowerCase());
	}
	
	/**
	 * HTTP headers are case insensitive. This method checks if a header name is the same as this header
	 * regardless of case.
	 */
	public boolean is(String name) {
		return this.name.equalsIgnoreCase(name);
	}
	
	/**
	 * This method checks if a header name and value match this header regardless of case.
	 */
	public boolean is(String name, String value) {
		return is(name) && this.value.equalsIgnoreCase(value);
	}
	
	/**
	 * A factory method to convert an array of headers to a map of canonical header name to a collection of headers.
	 *
	 * @see #toMap(Collection)
	 */
	public static Map<String, Collection<RexyHeader>> toMap(RexyHeader[] headers) {
		return toMap(Arrays.stream(headers));
	}
	
	/**
   	 * A factory method to convert a collection of headers to a map of canonical header name to a collection of
	 * headers. In the returned map a collection of headers is required as a single header may have multiple values.
	 */
	public static Map<String, Collection<RexyHeader>> toMap(Collection<RexyHeader> headers) {
		return toMap(headers.stream());
	}
	
	private static Map<String, Collection<RexyHeader>> toMap(Stream<RexyHeader> headers) {
		return headers.collect(Collectors.toMap(
				header -> header.name.toLowerCase(), Collections::singleton, collectionMerger()));
	}
	
	@Override
	public String toString() {
		return name + ": " + value;
	}
	
}
