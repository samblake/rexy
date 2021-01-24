package rexy.module.jmx.matcher;

import rexy.http.Method;


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
	 * @param method The HTTP request method
	 * @param path The URI path
	 * @return True if the MBean should handle the request, false otherwise
	 */
	boolean matches(Method method, String path);
	
}