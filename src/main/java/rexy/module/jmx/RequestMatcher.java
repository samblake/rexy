package rexy.module.jmx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.model.Api;
import rexy.config.model.Endpoint;
import rexy.http.Method;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Associates an MBean with a pattern. Checks possible paths to test if they match the pattern.
 * @param <T> The type of MBean
 */
public final class RequestMatcher<T> {
	private static final Logger logger = LogManager.getLogger(JmxRegistry.class);
	
	private static final Pattern PATTERN = Pattern.compile("\\{.+?}");
	
	private final Method method;
	private final Pattern pattern;
	private final T mBean;
	
	private RequestMatcher(Method method, Pattern pattern, T mBean) {
		this.method = method;
		this.pattern = pattern;
		this.mBean = mBean;
	}
	
	/**
	 * Creates a path matcher for an {@link Api} and {@link Endpoint}.
	 */
	public static <T> RequestMatcher<T> create(Endpoint endpoint, T mBean) {
		Method method = endpoint.getMethod() == null ? Method.GET : endpoint.getMethod();
		Matcher matcher = PATTERN.matcher(escape(endpoint.getEndpoint()));
		String regex = '/' + endpoint.getApi().getBaseUrl() + matcher.replaceAll(".+?");
		return new RequestMatcher<>(method, Pattern.compile(regex), mBean);
	}
	
	private static String escape(String endpoint) {
		// TODO better escaping
		return endpoint.replace("?", "\\?");
	}
	
	public T getMBean() {
		return mBean;
	}
	
	public Pattern getPattern() {
		return pattern;
	}
	
	public boolean matches(Method method, String path) {
		if (logger.isDebugEnabled()) {
			logger.debug("Testing endpoint " + pattern);
		}
		
		return this.method == method && pattern.matcher(path).matches();
	}
	
}