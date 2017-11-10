package rexy.feature.jmx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.model.Api;
import rexy.config.model.Endpoint;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.LinkedList;
import java.util.List;

/**
 * A registry for MBeans of a particular type. It will register an MBean for an endpoint as well as
 * retrieve the MBean for a request.
 *
 * @param <T> The type of MBean to store
 */
public abstract class JmxRegistry<T> {
	private static final Logger logger = LogManager.getLogger(JmxRegistry.class);
	
	private final List<PathMatcher<T>> repo = new LinkedList<>();
	
	/**
	 * Creates and registers an MBean for an endpoint.
	 *
	 * @param api      The API the endpoint is for
	 * @param endpoint The endpoint to register an MBean for
	 * @return The registered MBean
	 *
	 * @throws JMException Thrown if the MBean cannot be registered
	 */
	public T addEndpoint(Api api, Endpoint endpoint) throws JMException {
		T mBean = createMBean(api, endpoint);
		registerMBean(api.getName(), endpoint.getName(), mBean);
		repo.add(PathMatcher.create(api, endpoint, mBean));
		return mBean;
	}
	
	/**
	 * Creates an MBean for an endpoint.
	 *
	 * @param api The API the endpoint is for
	 * @param endpoint The endpoint to register an MBean for
	 * @return The created MBean
	 */
	protected abstract T createMBean(Api api, Endpoint endpoint);
	
	protected void registerMBean(String type, String name, T endpoint) throws JMException {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		
		ObjectName objectName = new ObjectNameBuilder()
				.withType(type).withScope(name).withName("preset - " + getMBeanName()).build();
		server.registerMBean(endpoint, objectName);
	}

	/**
	 * Returns the name of an MBean. This is used as part of the {@link ObjectName JMX object name}.
	 *
	 * @return The MBean name
	 */
	protected abstract String getMBeanName();
	
	/**
	 * Finds the MBean associated with the given path.
	 *
	 * @param path The path to find the MBean for
	 * @return The MBean or null if one is not found
	 */
	public T getMBean(String path) {
		if (logger.isDebugEnabled()) {
			logger.debug("Finding endpoint for " + path);
		}
		
		for (PathMatcher<T> matcher : repo) {
			if (matcher.matches(path)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Matched endpoint " + matcher.getPattern());
				}
				return matcher.getmBean();
			}
		}

		logger.warn("No matching endpoint for " + path);
		return null;
	}
}