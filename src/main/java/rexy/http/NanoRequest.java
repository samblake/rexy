package rexy.http;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class NanoRequest implements RexyRequest {
	
	private final IHTTPSession session;
	private final String route;
	
	public NanoRequest(IHTTPSession session, String route) {
		this.session = session;
		this.route = route;
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
	
}