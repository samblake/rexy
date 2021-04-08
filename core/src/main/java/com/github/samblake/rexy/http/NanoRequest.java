package com.github.samblake.rexy.http;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import com.github.samblake.rexy.http.request.RexyRequest;
import fi.iki.elonen.NanoHTTPD.ResponseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.pdfbox.io.IOUtils.toByteArray;

/**
 * An adapter for a {@link NanoRequest} onto the generic {@link RexyRequest}.
 */
public class NanoRequest implements RexyRequest {
	
	private final IHTTPSession session;
	private final String route;
	private final String body;
	
	public NanoRequest(IHTTPSession session, String route) {
		this(session, route, null);
	}
	
	public NanoRequest(IHTTPSession session, String route, String body) {
		this.session = session;
		this.route = route;
		this.body = body;
	}
	
	@Override
	public String getUri() {
		return session.getUri();
	}
	
	@Override
	public String getContextPath() {
		return route;
	}
	
	@Override
	public String getQueryString() {
		return session.getQueryParameterString();
	}
	
	@Override
	public List<RexyHeader> getHeaders() {
		return session.getHeaders().entrySet().stream().map(RexyHeader::fromEntry).collect(toList());
	}
	
	@Override
	public Method getMethod() {
		return Method.valueOf(session.getMethod().name());
	}
	
	@Override
	public Map<String, String> getParameters() {
		return session.getParameters().entrySet().stream()
				.collect(toMap(Entry::getKey, entry -> entry.getValue().stream().findFirst().get()));
	}
	
	public String getBody() {
		return body;
	}
	
}