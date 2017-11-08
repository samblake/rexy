package rexy.feature.jmx.mock;

import java.util.Map;

public interface MockEndpointMBean {
	
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
