package rexy.feature.jmx.mock;

import rexy.config.model.Response;

public class MockEndpoint implements MockEndpointMBean {
	
	private boolean intercept;
	private String contentType;
	private int httpStatus;
	private String body;
	
	public MockEndpoint(String contentType, Response body) {
		this(contentType, body.getHttpStatus(), body.getBody().toString());
	}
	
	public MockEndpoint(String contentType, int httpStatus, String body) {
		this.intercept = false;
		this.httpStatus = httpStatus;
		this.contentType = contentType;
		this.body = body;
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
	public String getBody() {
		return body;
	}
	
	@Override
	public void setBody(String body) {
		this.body = body;
	}
	
	
}