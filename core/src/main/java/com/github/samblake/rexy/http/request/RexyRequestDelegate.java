package com.github.samblake.rexy.http.request;

import com.github.samblake.rexy.http.Method;
import com.github.samblake.rexy.http.RexyHeader;

import java.util.Collection;
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
	public Collection<RexyHeader> getHeaders() {
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
	
	@Override
	public String getBody() {
		return delegate.getBody();
	}
	
}