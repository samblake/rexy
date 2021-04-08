package com.github.samblake.rexy.http.request;

import com.github.samblake.rexy.http.Method;
import com.github.samblake.rexy.http.RexyHeader;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Models HTTP requests within Rexy.
 */
public interface RexyRequest {
	
	/**
	 * The URL path.
	 */
	String getUri();
	
	/**
	 * The context path of the URL. This is the base path Rexy is configured to run under.
	 */
	String getContextPath();
	
	/**
	 * The query string.
	 */
	String getQueryString();
	
	/**
	 * The headers as a list of {@link RexyHeader Rexy headers}.
	 */
	List<RexyHeader> getHeaders();
	
	/**
	 * The HTTP method.
	 */
	Method getMethod();
	
	/**
	 * The request parameters in a map from name to value. If multiple values exist for the same
	 * parameter then only the first occurrence of the parameter will be returned.
	 */
	Map<String, String> getParameters();
	
	/**
	 * The request body encoded as a byte array.
	 */
	String getBody();
	
}