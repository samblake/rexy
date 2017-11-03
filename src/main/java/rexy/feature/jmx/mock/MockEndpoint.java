package rexy.feature.jmx.mock;

import rexy.config.model.Response;

public class MockEndpoint implements MockEndpointMBean {
	
	private boolean intercept;
	private String contentType;
	private int httpStatus;
	private String response;
	
	public MockEndpoint(String contentType, Response response) {
		this(contentType, response.getHttpStatus(), response.getBody().toString());
	}
	
	public MockEndpoint(String contentType, int httpStatus, String response) {
		this.intercept = false;
		this.httpStatus = httpStatus;
		this.contentType = contentType;
		this.response = response;
	}
	
	@Override
	public boolean isIntercept() {
		return intercept;
	}
	
	@Override
	public void setIntercept(boolean intercept) {
		this.intercept = intercept;
	}
	
	@Override
	public String getContentType() {
		return contentType;
	}
	
	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	@Override
	public int getHttpStatus() {
		return httpStatus;
	}
	
	@Override
	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}
	
	@Override
	public String getResponse() {
		return response;
	}
	
	@Override
	public void setResponse(String response) {
		this.response = response;
	}
	
	
}