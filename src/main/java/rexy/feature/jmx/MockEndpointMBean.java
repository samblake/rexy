package rexy.feature.jmx;

public interface MockEndpointMBean {

	boolean isIntercept();
	
	void setIntercept(boolean intercept);
	
	String getContentType();
	
	void setContentType(String contentType);
	
	int getHttpStatus();
	
	void setHttpStatus(int httpStatus);
	
	String getResponse();
	
	void setResponse(String response);
}
