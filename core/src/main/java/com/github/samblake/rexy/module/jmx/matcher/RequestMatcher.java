package com.github.samblake.rexy.module.jmx.matcher;

import com.github.samblake.rexy.http.request.RexyRequest;


/**
 * Associates an MBean with a set of requests.
 *
 * @param <T> The type of MBean
 */
public interface RequestMatcher<T> {
	
	/**
	 * Returns the MBean associated with the matcher.
	 */
	T getMBean();
	
	/**
	 * Checks to see if the MBean should handle the request.
	 *
	 * @param request The request to check
	 * @return True if the MBean should handle the request, false otherwise
	 */
	boolean matches(RexyRequest request);
	
}