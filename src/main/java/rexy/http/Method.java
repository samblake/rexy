package rexy.http;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Modules HTTP methods within Rexy.
 */
public enum Method {
	GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, TRACE, CONNECT;
	
	@JsonCreator
	public static Method from(String name) {
		return valueOf(name.toUpperCase());
	}
	
}