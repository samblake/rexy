package rexy.module.jmx.matcher;

import rexy.config.model.Api;
import rexy.config.model.Endpoint;

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
		return RegexRequestMatcher.create(endpoint, mBean);
	}
	
}
