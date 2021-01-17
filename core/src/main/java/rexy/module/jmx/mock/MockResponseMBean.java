package rexy.module.jmx.mock;

import java.util.Map;

/**
 * The preset mock response MBean.
 */
public interface MockResponseMBean {
	
	int getHttpStatus();
	
	Map<String, String> getHeaders();
	
	String getBody();
	
	/**
	 * Sets the preset as the current mock response. May also set it to intercept the request
	 * depending on configuration.
	 */
	void set();
	
}