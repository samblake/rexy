package rexy.feature.jmx;

import rexy.config.model.Response;

public class MockResponse implements MockResponseMBean {
	
	private final MockEndpoint endpoint;
	private int httpStatus;
	private String response;
	
	public MockResponse(MockEndpoint endpoint, Response response) {
		this(endpoint, response.getHttpStatus(), response.getBody(). toString());
	}
	
	public MockResponse(MockEndpoint endpoint, int httpStatus, String response) {
		this.endpoint = endpoint;
		this.httpStatus = httpStatus;
		this.response = response;
	}
	
	@Override
	public int getHttpStatus() {
		return httpStatus;
	}
	
	@Override
	public String getResponse() {
		return response;
	}
	
	public void set() {
		endpoint.setHttpStatus(httpStatus);
		endpoint.setResponse(response);
	}
}