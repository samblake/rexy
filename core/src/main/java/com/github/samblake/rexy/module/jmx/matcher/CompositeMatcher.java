package com.github.samblake.rexy.module.jmx.matcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.samblake.rexy.annotations.Internal;
import com.github.samblake.rexy.http.request.RexyRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * <p>Checks if all of a collection of matchers match the current request. For internal use only.
 *
 * @param <T> The type of MBean
 */
@Internal
public final class CompositeMatcher<T> extends AbstractRequestMatcher<T> {
	
	private final Collection<RequestMatcher<T>> matchers;
	
	private CompositeMatcher(T mBean, Collection<RequestMatcher<T>> matchers) {
		super(mBean);
		this.matchers = matchers;
	}
	
	static <T> RequestMatcher<T> create(T mBean, Map<String, JsonNode> matcherConfig) {
		List<RequestMatcher<T>> matchers = matcherConfig.entrySet().stream()
				.map(e -> MatcherFactory.create(e.getKey(), e.getValue(), mBean))
				.collect(toList());
		
		return create(mBean, matchers);
	}
	
	static <T> RequestMatcher<T> create(T mBean, Collection<RequestMatcher<T>> matchers) {
		return new CompositeMatcher<>(mBean, matchers);
	}
	
	@Override
	public boolean matches(RexyRequest request) {
		for (RequestMatcher<T> matcher : matchers) {
			if (!matcher.matches(request)) {
				return false;
			}
		}
		return true;
	}
	
}