package rexy.module.jmx.mock;

import org.apache.log4j.Logger;
import rexy.config.model.Response;
import rexy.utils.Bodies;

import java.util.Map;

import static rexy.utils.Bodies.findBody;

/**
 * The mock endpoint implementation for the {@link MockModule}.
 */
public class MockEndpoint implements MockEndpointMBean {
	private static final Logger logger = Logger.getLogger(MockEndpoint.class);
	
	private boolean intercept;
	private String contentType;
	private int httpStatus;
	private String body;
	private Map<String, String> headers;
	
	public MockEndpoint(String contentType, Response response, boolean intercept) {
		this(contentType, response.getHttpStatus(), response.getHeaders(), findBody(response), intercept);
	}
	
	public MockEndpoint(String contentType, int httpStatus,
			Map<String, String> headers, String body, boolean intercept) {
		
		this.httpStatus = httpStatus;
		this.contentType = contentType;
		this.headers = headers;
		this.body = body;
		this.intercept = intercept;
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