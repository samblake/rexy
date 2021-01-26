package com.github.samblake.rexy.module.jmx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.github.samblake.rexy.config.model.Endpoint;
import com.github.samblake.rexy.http.Method;
import com.github.samblake.rexy.module.jmx.matcher.MatcherFactory;
import com.github.samblake.rexy.module.jmx.matcher.RequestMatcher;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

/**
 * A registry for MBeans of a particular type. It is used to register an MBean for an endpoint as well as
 * retrieve the MBean for a request.
 *
 * @param <T> The type of MBean to store
 */
public abstract class JmxRegistry<T> {
	private static final Logger logger = LogManager.getLogger(JmxRegistry.class);
	
	private final List<RequestMatcher<T>> repo = new ArrayList<>();
	
	/**
	 * Creates and registers an MBean for an endpoint.
	 *
	 * @param endpoint The endpoint to register an MBean against
	 * @return The registered MBean
	 *
	 * @throws JMException Thrown if the MBean cannot be registered
	 */
	public T addEndpoint(Endpoint endpoint) throws JMException {
		T mBean = createMBean(endpoint);
		registerMBean(endpoint.getApi().getName(), endpoint.getName(), mBean);
		repo.add(MatcherFactory.create(endpoint, mBean));
		return mBean;
	}
	
	/**
	 * Creates an MBean for an endpoint.
	 *
	 * @param endpoint The endpoint to register an MBean against
	 * @return The created MBean
	 */
	protected abstract T createMBean(Endpoint endpoint);
	
	/**
	 * Registers an MBean with the MBean server.
	 */
	protected void registerMBean(String apiName, String endpointName, T endpoint) throws JMException {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		
		ObjectName objectName = new ObjectNameBuilder()
				.withApi(apiName)
				.withEndpoint(endpointName)
				.withName(getMBeanName())
				.build();
		
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
	 * @param method The HTTP method find the MBean for
	 * @param path The path to find the MBean for
	 * @return The MBean or empty if one is not found
	 */
	public Optional<T> getMBean(Method method, String path) {
		if (logger.isDebugEnabled()) {
			logger.debug("Finding endpoint for " + path);
		}
		
		Optional<RequestMatcher<T>> matcher = repo.stream()
				.filter((m) -> m.matches(method, path))
				.findFirst();
		
		if (matcher.isPresent()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Matched endpoint " + matcher);
			}
			return matcher.map(RequestMatcher::getMBean);
		}

		logger.warn("No matching endpoint for " + path);
		return empty();
	}
}