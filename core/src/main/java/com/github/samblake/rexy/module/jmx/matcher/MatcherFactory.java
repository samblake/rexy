package com.github.samblake.rexy.module.jmx.matcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.samblake.rexy.config.model.Api;
import com.github.samblake.rexy.config.model.Endpoint;

/**
 * <p>Creates {@link RequestMatcher request matchers}.</p>
 *
 * <p>In the future there may be other matchers that have different rules. For example matching parameters
 * in any order, treating parameters as optional, etc.</p>
 */
public final class MatcherFactory {
	
	private MatcherFactory() {
	}
	
	/**
	 * Creates a path matcher for an {@link Api} and {@link Endpoint}.
	 *
	 * @param endpoint The endpoint to create to matcher for
	 * @param mBean The MBean associated with that endpoint
	 * @param <T> The type of MBean
	 * @return The created matcher
	 */
	public static <T> RequestMatcher<T> create(Endpoint endpoint, T mBean) {
		RequestMatcher<T> requestMatcher = RegexRequestMatcher.create(endpoint, mBean);
		if (endpoint.getMatchers() == null || endpoint.getMatchers().isEmpty()) {
			return requestMatcher;
		}
		
		RequestMatcher<T> compositeMatcher = CompositeMatcher.create(mBean, endpoint.getMatchers());
		return BiMatcher.create(mBean, requestMatcher, compositeMatcher);
	}
	
	public static <T> RequestMatcher<T> create(String name, JsonNode config, T mBean) {
		switch (name) {
			case "header":
				return HeaderMatcher.create(config, mBean);
			case "regex":
				return RegexBodyMatcher.create(config, mBean);
			case "xpath":
				return XpathBodyMatcher.create(config, mBean);
			case "jsonpath":
				return JsonPathBodyMatcher.create(config, mBean);
			case "not":
				return NotMatcher.create(config, mBean);
			default:
				// FIXME non-runtime exception
				throw new RuntimeException("Unknown matcher: " + name);
		}
	}
	
}
