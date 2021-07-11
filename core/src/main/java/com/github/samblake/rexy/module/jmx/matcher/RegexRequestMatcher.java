package com.github.samblake.rexy.module.jmx.matcher;

import com.github.samblake.rexy.config.model.Api;
import com.github.samblake.rexy.config.model.Endpoint;
import com.github.samblake.rexy.http.Method;
import com.github.samblake.rexy.http.request.RexyRequest;
import com.github.samblake.rexy.module.jmx.JmxRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * <p>The base matcher that is applied to every request. Associates an MBean with a HTTP method and a regular
 * expression pattern that is matched against the requested URL.
 *
 * @param <T> The type of MBean
 */
public final class RegexRequestMatcher<T> extends AbstractRequestMatcher<T> {
	private static final Logger logger = LogManager.getLogger(JmxRegistry.class);
	
	private static final Pattern PATTERN = compile("\\{.+?}");
	
	private final Method method;
	private final Pattern pattern;
	
	private RegexRequestMatcher(Method method, Pattern pattern, T mBean) {
		super(mBean);
		this.method = method;
		this.pattern = pattern;
	}
	
	/**
	 * <p>Creates a path matcher for an {@link Api} and {@link Endpoint}. The pattern is created from the endpoint.
	 * For example:</p>
	 *
	 * <pre><code>
	 * {@code
	 *     /path?abc={abc}&123={123}
	 * }
	 * </code></pre>
	 *
	 * <p>will be converted to a pattern that matches the path with any values for the parameters. However, it
	 * should be noted that parameter order is important.</p>
	 */
	static <T> RequestMatcher<T> create(Endpoint endpoint, T mBean) {
		String escaped = endpoint.getEndpoint().replace("?", "\\?");
		Matcher matcher = PATTERN.matcher(escaped);
		// TODO what about empty values .*?, maybe have it configurable
		String regex = '/' + endpoint.getApi().getBaseUrl() + matcher.replaceAll(".+?");
		return new RegexRequestMatcher<>(endpoint.getMethod(), compile(regex), mBean);
	}
	
	@Override
	public boolean matches(RexyRequest request) {
		if (logger.isDebugEnabled()) {
			logger.debug("Testing endpoint " + pattern);
		}
		
		return method == request.getMethod() && pattern.matcher(request.getPath()).matches();
	}
	
	@Override
	public String toString() {
		return super.toString() + ':' + pattern;
	}
	
}