package rexy.feature.jmx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.model.Api;
import rexy.config.model.Endpoint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathMatcher<T> {
	private static final Logger logger = LogManager.getLogger(JmxRegistry.class);
	
	private final Pattern pattern;
	private final T mBean;
	
	private PathMatcher(Pattern pattern, T mBean) {
		this.pattern = pattern;
		this.mBean = mBean;
	}
	
	public static <T> PathMatcher<T> create(Api api, Endpoint endpoint, T mBean) {
		Pattern pattern = Pattern.compile("\\{.+?\\}");
		Matcher matcher = pattern.matcher(escape(endpoint.getEndpoint()));
		String regex = "/" + api.getBaseUrl() + matcher.replaceAll(".+?");
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