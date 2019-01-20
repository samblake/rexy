package rexy.http.request;

import rexy.http.Method;
import rexy.http.RexyHeader;

import java.util.List;
import java.util.Map;

/**
 * Delegates all method calls to the delegate.
 */
public abstract class RexyRequestDelegate implements RexyRequest {
	
	private final RexyRequest delegate;
	
	protected RexyRequestDelegate(RexyRequest delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public String getUri() {
		return delegate.getUri();
	}
	
	@Override
	public String getContextPath() {
		return delegate.getContextPath();
	}
	
	@Override
	public String getQueryString() {
		return delegate.getQueryString();
	}
	
	@Override
	public List<RexyHeader> getHeaders() {
		return delegate.getHeaders();
	}
	
	@Override
	public Method getMethod() {
		return delegate.getMethod();
	}
	
	@Override
	public Map<String, String> getParameters() {
		return delegate.getParameters();
	}
	
}