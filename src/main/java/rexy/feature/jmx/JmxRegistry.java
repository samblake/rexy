package rexy.feature.jmx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rexy.config.model.Api;
import rexy.config.model.Endpoint;

import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.OperationsException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class JmxRegistry<T> {
	private static final Logger logger = LoggerFactory.getLogger(JmxRegistry.class);
	
	private final Map<Pattern, T> repo = new HashMap<>();
	
	protected JmxRegistry() {
	}
	
	public T addEndpoint(Api api, Endpoint endpoint) throws OperationsException, MBeanRegistrationException {
		T mockEndpoint = createMBean(api, endpoint);
		registerMBean(api.getName(), endpoint.getName(), mockEndpoint);
		repo.put(createRegex(api, endpoint), mockEndpoint);
		return mockEndpoint;
	}
	
	protected abstract T createMBean(Api api, Endpoint endpoint);
	
	protected void registerMBean(String type, String name, T endpoint)
			throws OperationsException, MBeanRegistrationException {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		ObjectName objectName = new ObjectName("Rexy:type=" + type + ",scope=" + name + ",name=" + getMBeanName());
		server.registerMBean(endpoint, objectName);
	}

	protected abstract String getMBeanName();

	private Pattern createRegex(Api api, Endpoint endpoint) {
		Pattern pattern = Pattern.compile("\\{.+?\\}");
		Matcher matcher = pattern.matcher(escape(endpoint.getEndpoint()));
		String regex = ".*" + api.getBaseUrl() + matcher.replaceAll(".+?");
		return Pattern.compile(regex);
	}
	
	private String escape(String endpoint) {
		// TODO better escaping
		return endpoint.replace("?", "\\?");
	}
	
	public T getEndpoint(String path) {
		if (logger.isDebugEnabled()) {
			logger.debug("Getting endpoint for " + path);
		}

		for (Entry<Pattern, T> entry : repo.entrySet()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Testing endpoint " + entry.getKey().toString());
			}

			if (entry.getKey().matcher(path).matches()) {
				return entry.getValue();
			}
		}

		logger.debug("No matching endpoint for " + path);
		return null;
	}
}