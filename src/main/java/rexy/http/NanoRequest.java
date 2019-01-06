package rexy.http;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import rexy.http.request.RexyRequest;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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
	
	@Override
	public Map<String, String> getParameters() {
		return session.getParameters().entrySet().stream()
				.collect(toMap(Entry::getKey, entry -> entry.getValue().stream().findFirst().get()));
	}
	
}