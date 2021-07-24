package com.github.samblake.rexy.module.template;

import com.github.samblake.rexy.http.Method;
import com.github.samblake.rexy.http.RexyHeader;
import com.github.samblake.rexy.http.request.RexyRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.samblake.rexy.http.Method.GET;
import static java.util.stream.Collectors.joining;

public class MockRequest implements RexyRequest {
	
	private String uri = "";
	private String contextPath = "";
	private List<RexyHeader> headers = new ArrayList<>();
	private Method method = GET;
	private Map<String, String> parameters = new HashMap<>();
	private String body;
	
	public MockRequest() {
	}
	
	@Override
	public String getUri() {
		return uri;
	}
	
	public MockRequest withUri(String uri) {
		this.uri = uri;
		return this;
	}
	
	@Override
	public String getContextPath() {
		return contextPath;
	}
	
	public MockRequest withContextPath(String contextPath) {
		this.contextPath = contextPath;
		return this;
	}
	
	@Override
	public String getQueryString() {
		return parameters.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(joining("&"));
	}
	
	@Override
	public List<RexyHeader> getHeaders() {
		return headers;
	}
	
	public MockRequest withHeaders(List<RexyHeader> headers) {
		this.headers.addAll(headers);
		return this;
	}
	
	public MockRequest withHeader(RexyHeader header) {
		this.headers.add(header);
		return this;
	}
	
	public MockRequest withHeader(String name, String value) {
		return withHeader(new RexyHeader(name, value));
	}
	
	@Override
	public Method getMethod() {
		return method;
	}
	
	public MockRequest withMethod(Method method) {
		this.method = method;
		return this;
	}
	
	@Override
	public Map<String, String> getParameters() {
		return parameters;
	}
	
	public MockRequest withParameters(Map<String, String> parameters) {
		this.parameters.putAll(parameters);
		return this;
	}
	
	public MockRequest withParameter(String name, String value) {
		this.parameters.put(name, value);
		return this;
	}
	
	@Override
	public String getBody() {
		return body;
	}
	
	public MockRequest withBody(String body) {
		this.body = body;
		return this;
	}
	
}