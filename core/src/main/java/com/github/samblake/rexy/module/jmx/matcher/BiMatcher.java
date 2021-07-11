package com.github.samblake.rexy.module.jmx.matcher;

import com.github.samblake.rexy.annotations.Internal;
import com.github.samblake.rexy.http.request.RexyRequest;

/**
 * <p>Checks if both of a pair of matchers match the current request. For internal use only.
 *
 * @param <T> The type of MBean
 */
@Internal
public final class BiMatcher<T> extends AbstractRequestMatcher<T> {
	
	private final RequestMatcher<T> first;
	private final RequestMatcher<T> second;
	
	private BiMatcher(T mBean, RequestMatcher<T> first, RequestMatcher<T> second) {
		super(mBean);
		this.first = first;
		this.second = second;
	}
	
	static <T> RequestMatcher<T> create(T mBean, RequestMatcher<T> first, RequestMatcher<T> second) {
		return new BiMatcher<>(mBean, first, second);
	}
	
	@Override
	public boolean matches(RexyRequest request) {
		return first.matches(request) && second.matches(request);
	}
	
}