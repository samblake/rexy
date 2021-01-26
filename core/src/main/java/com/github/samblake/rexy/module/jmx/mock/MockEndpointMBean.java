package com.github.samblake.rexy.module.jmx.mock;

import java.util.Map;

/**
 * The mock endpoint MBean for the {@link MockModule}.
 */
public interface MockEndpointMBean {
	
	/**
	 * If the the request should be intercepted and the mock response returned.
	 */
	boolean isIntercept();
	
	void setIntercept(boolean intercept);
	
	String getContentType();
	
	void setContentType(String contentType);
	
	int getHttpStatus();
	
	void setHttpStatus(int httpStatus);
	
	Map<String, String> getHeaders();
	
	void setHeaders(Map<String, String> headers);
	
	String getBody();
	
	void setBody(String body);

}