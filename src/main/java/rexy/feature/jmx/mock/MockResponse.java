package rexy.feature.jmx.mock;

import rexy.config.model.Response;

public class MockResponse implements MockResponseMBean {
	
	private final MockEndpoint endpoint;
	private final int httpStatus;
	private final String response;
	private final boolean interceptOnSet;
	
	public MockResponse(MockEndpoint endpoint, Response response, boolean interceptOnSet) {
		this(endpoint, response.getHttpStatus(), response.getBody().toString(), interceptOnSet);
	}
	
	public MockResponse(MockEndpoint endpoint, int httpStatus, String response, boolean interceptOnSet) {
		this.endpoint = endpoint;
		this.httpStatus = httpStatus;
		this.response = response;
		this.interceptOnSet = interceptOnSet;
	}
	
	@Override
	public int getHttpStatus() {
		return httpStatus;
	}
	
	@Override
	public String getResponse() {
		return response;
	}
	
	@Override
	public void set() {
		endpoint.setHttpStatus(httpStatus);
		endpoint.setBody(response);
		
		if (interceptOnSet) {
			endpoint.setIntercept(true);
		}
	}
}