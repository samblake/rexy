package rexy.module.jmx.mock;

import rexy.config.model.Response;

import java.util.Map;

public class MockEndpoint implements MockEndpointMBean {
	
	private boolean intercept;
	private String contentType;
	private int httpStatus;
	private String body;
	private Map<String, String> headers;
	
	public MockEndpoint(String contentType, Response response) {
		this(contentType, response.getHttpStatus(), response.getHeaders(), response.getBody().toString());
	}
	
	public MockEndpoint(String contentType, int httpStatus, Map<String, String> headers, String body) {
		this.intercept = false;
		this.httpStatus = httpStatus;
		this.contentType = contentType;
		this.headers = headers;
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
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	@Override
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
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