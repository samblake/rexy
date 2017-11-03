package rexy.feature.jmx;

import rexy.config.model.Api;
import rexy.config.model.Endpoint;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A registry for MBeans of a particular type. It will register an MBean for an endpoint as well as
 * retrieve the MBean for a request.
 *
 * @param <T> The type of MBean to store
 */
public abstract class JmxRegistry<T> {
	
	private final Map<Pattern, T> repo = new HashMap<>();
	
	protected JmxRegistry() {
	}
	
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
		repo.put(createRegex(api, endpoint), mBean);
		return mBean;
	}
	
	/**
	 * Creates an MBean for an endpoint.
	 
	 * @param api The API the endpoint is for
	 * @param endpoint The endpoint to register an MBean for
	 * @return The created MBean
	 */
	protected abstract T createMBean(Api api, Endpoint endpoint);
	
	protected void registerMBean(String type, String name, T endpoint) throws JMException {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		ObjectName objectName = new ObjectName("Rexy:type=" + type + ",scope=" + name + ",name=" + getMBeanName());
		server.registerMBean(endpoint, objectName);
	}
	
	/**
	 * Returns the name of an MBean. This is used as part of the {@link ObjectName JMX object name}.
	 *
	 * @return The MBean name
	 */
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
	
	/**
	 * Finds the MBean associated with the given path.
	 *
	 * @param path The path to find the MBean for
	 * @return The MBean or null if one is not found
	 */
	public T getMBean(String path) {
		for (Entry<Pattern, T> entry : repo.entrySet()) {
			if (entry.getKey().matcher(path).matches()) {
				return entry.getValue();
			}
		}
		return null;
	}
}