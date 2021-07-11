package com.github.samblake.rexy.module.jmx.matcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.samblake.rexy.http.request.RexyRequest;

import java.util.Map.Entry;

/**
 * <p>Inverts a request matcher to match when a request does not match the enclosed matcher. For example:
 *
 * <pre><code>{@code
 * "matchers": {
 *   "not": {
 *     "header": {
 *        "Accept": "application/xhtml+xml"
 *     }
 *   }
 * },
 * }</code></pre>
 *
 * <p>This would match a request that doesn't contain the header specified in the request matcher.
 *
 * @param <T> The type of MBean
 */
public final class NotMatcher<T> extends AbstractRequestMatcher<T> {
	
	private final RequestMatcher<T> matcher;
	
	private NotMatcher(T mBean, RequestMatcher<T> matcher) {
		super(mBean);
		this.matcher = matcher;
	}
	
	static <T> RequestMatcher<T> create(JsonNode config, T mBean) {
		if (!config.isObject()) {
			throw new RuntimeException("Invalid config, object expected");
		}
		
		if (config.size() != 1) {
			throw new RuntimeException("Invalid config, single child expected");
		}
		
		try {
			Entry<String, JsonNode> entry = config.fields().next();
			RequestMatcher<T> matcher = MatcherFactory.create(entry.getKey(), entry.getValue(), mBean);
			return new NotMatcher<>(mBean, matcher);
		}
		catch (RuntimeException e) {
			throw new RuntimeException("Invalid config, invalid matcher");
		}
	}
	
	@Override
	public boolean matches(RexyRequest request) {
		return !matcher.matches(request);
	}
	
}