package com.github.samblake.rexy.module.jmx.matcher;

import com.github.samblake.rexy.http.request.RexyRequest;

/**
 * An abstract request matcher for matching a request's body.
 *
 * @param <T> The type of MBean
 */
public abstract class RequestBodyMatcher<T> extends AbstractRequestMatcher<T> {
	
	public RequestBodyMatcher(T mBean) {
		super(mBean);
	}
	
	@Override
	public boolean matches(RexyRequest request) {
		return request.getBody() != null && matches(request.getBody());
	}
	
	/**
	 * Checks to see if the MBean should handle the request.
	 *
	 * @param body The request to check
	 * @return True if the MBean should handle the request, false otherwise
	 */
	protected abstract boolean matches(String body);
	
	@Override
	public String toString() {
		return super.toString() + ":body";
	}
	
}