package rexy.http;

import java.util.Map.Entry;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;

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
	
	public static RexyHeader fromEntry(Entry<String, String> entry) {
		return new RexyHeader(entry.getKey(), entry.getValue());
	}
	
	public boolean isProxyable() {
		return !STRIP_HEADERS.contains(name.toLowerCase());
	}
	
}
