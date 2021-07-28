package com.github.samblake.rexy.mocks;

import com.github.samblake.rexy.http.RexyHeader;
import com.github.samblake.rexy.http.response.RexyResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.samblake.rexy.utils.Streams.flattern;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class MockResponse implements RexyResponse {
	
	private int statusCode = 200;
	private String mimeType = "application/json";
	private String body = "";
	private Map<String, Collection<RexyHeader>> headers = new HashMap<>();
	
	public MockResponse() {
	}
	
	public MockResponse withStatusCode(int statusCode) {
		this.statusCode = statusCode;
		return this;
	}
	
	@Override
	public int getStatusCode() {
		return statusCode;
	}
	
	public MockResponse withMimeType(String mimeType) {
		this.mimeType = mimeType;
		return this;
	}
	
	@Override
	public String getMimeType() {
		return mimeType;
	}
	
	public MockResponse withHeader(RexyHeader header) {
		headers.compute(header.getName(), (k, v) -> (v == null) ? List.of(header)
				: concat(v.stream(), Stream.of(header)).collect(toList()));
		return this;
	}
	
	public MockResponse withHeader(String name, String value) {
		return withHeader(new RexyHeader(name, value));
	}
	
	@Override
	public Collection<RexyHeader> getHeaders() {
		return flattern(headers.values());
	}
	
	public MockResponse withBody(String body) {
		this.body = body;
		return this;
	}
	
	@Override
	public InputStream getBody() {
		return new ByteArrayInputStream(body.getBytes(UTF_8));
	}
	
	@Override
	public int getResponseLength() {
		return body.getBytes(UTF_8).length;
	}
	
	@Override
	public Map<String, Collection<RexyHeader>> getHeaderMap() {
		return headers;
	}
	
}