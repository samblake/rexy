package rexy.module.jmx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.model.Api;
import rexy.config.model.Endpoint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Associates an MBean with a pattern. Checks possible paths to test if they match the pattern.
 * @param <T> The type of MBean
 */
public class PathMatcher<T> {
	private static final Logger logger = LogManager.getLogger(JmxRegistry.class);
	
	private final Pattern pattern;
	private final T mBean;
	
	private PathMatcher(Pattern pattern, T mBean) {
		this.pattern = pattern;
		this.mBean = mBean;
	}
	
	/**
	 * Creates a path matcher for an {@link Api} and {@link Endpoint}.
	 *
	 * @param <T>
	 * @param endpoint The endpoint associated with the
	 * @param mBean
	 * @return
	 */
	public static <T> PathMatcher<T> create(Endpoint endpoint, T mBean) {
		Pattern pattern = Pattern.compile("\\{.+?\\}");
		Matcher matcher = pattern.matcher(escape(endpoint.getEndpoint()));
		String regex = "/" + endpoint.getApi().getBaseUrl() + matcher.replaceAll(".+?");
		return new PathMatcher<T>(Pattern.compile(regex), mBean);
	}
	
	private static String escape(String endpoint) {
		// TODO better escaping
		return endpoint.replace("?", "\\?");
	}
	
	public T getmBean() {
		return mBean;
	}
	
	public Pattern getPattern() {
		return pattern;
	}
	
	public boolean matches(String path) {
		if (logger.isDebugEnabled()) {
			logger.debug("Testing endpoint " + pattern);
		}
		
		return (pattern.matcher(path).matches());
	}
	
}