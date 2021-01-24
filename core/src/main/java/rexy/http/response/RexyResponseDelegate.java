package rexy.http.response;

import rexy.http.RexyHeader;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * Delegates all method calls to the delegate.
 */
public abstract class RexyResponseDelegate implements RexyResponse {

	private final RexyResponse delegate;
	
	protected RexyResponseDelegate(RexyResponse delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public int getStatusCode() {
		return delegate.getStatusCode();
	}
	
	@Override
	public String getMimeType() {
		return delegate.getMimeType();
	}
	
	@Override
	public Collection<RexyHeader> getHeaders() {
		return delegate.getHeaders();
	}
	
	@Override
	public InputStream getBody() {
		return delegate.getBody();
	}

	@Override
	public int getResponseLength() {
		return delegate.getResponseLength();
	}
	
	@Override
	public Map<String, Collection<RexyHeader>> getHeaderMap() {
		return delegate.getHeaderMap();
	}

}