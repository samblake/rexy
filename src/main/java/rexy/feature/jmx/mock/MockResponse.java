package rexy.feature.jmx.mock;

import rexy.config.model.Response;

import java.util.Map;

public class MockResponse implements MockResponseMBean {
	
	private final MockEndpoint endpoint;
	private final int httpStatus;
	private final Map<String, String> headers;
	private final String body;
	private final boolean interceptOnSet;
	
	public MockResponse(MockEndpoint endpoint, Response body, boolean interceptOnSet) {
		this(endpoint, body.getHttpStatus(), body.getHeaders(), body.getBody().toString(), interceptOnSet);
	}
	
	public MockResponse(MockEndpoint endpoint, int httpStatus, Map<String, String> headers, String body, boolean interceptOnSet) {
		this.endpoint = endpoint;
		this.httpStatus = httpStatus;
		this.headers = headers;
		this.body = body;
		this.interceptOnSet = interceptOnSet;
	}
	
	@Override
	public int getHttpStatus() {
		return httpStatus;
	}
	
	@Override
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	@Override
	public String getBody() {
		return body;
	}
	
	@Override
	public void set() {
		endpoint.setHttpStatus(httpStatus);
		endpoint.setBody(body);
		endpoint.setHeaders(headers);
		
		if (interceptOnSet) {
			endpoint.setIntercept(true);
		}
	}
}