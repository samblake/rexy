package rexy.http;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

public class Headers {
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
	
	public static final Set<String> STRIP_HEADERS = unmodifiableSet(new HashSet<>(asList(
			HEADER_HOST,
			HEADER_CONNECTION,
			HEADER_TE,
			HEADER_TRANSFER_ENCODING,
			HEADER_KEEP_ALIVE,
			HEADER_PROXY_AUTHORIZATION,
			HEADER_PROXY_AUTHENTICATION,
			HEADER_TRAILER,
			HEADER_UPGRADE
	)));
	
}