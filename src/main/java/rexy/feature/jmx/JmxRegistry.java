package rexy.feature.jmx;

import rexy.config.Api;
import rexy.config.Endpoint;
import rexy.config.Response;

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

public final class JmxRegistry {
	
	private static final JmxRegistry INSTANCE = new JmxRegistry();
	
	private final Map<Pattern, MockEndpoint> repo = new HashMap<>();
	
	private JmxRegistry() {
	}
	
	public static JmxRegistry getInstance() {
		return INSTANCE;
	}
	
	public void addEndpoint(Api api, Endpoint endpoint) throws OperationsException, MBeanRegistrationException {
		Response response = endpoint.getResponses().iterator().next();
		MockEndpoint mockEndpoint = new MockEndpoint(api.getContentType(), response.getHttpStatus(),
				response.getBody().toString());
		registerMBean(api.getName(), endpoint.getName(), mockEndpoint);
		repo.put(createRegex(api, endpoint), mockEndpoint);
	}
	
	private void registerMBean(String type, String name, MockEndpoint endpoint)
			throws OperationsException, MBeanRegistrationException {
		
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		ObjectName objectName = new ObjectName("Rexy:type=" + type + ",name=" + name);
		server.registerMBean(endpoint, objectName);
	}
	
	private Pattern createRegex(Api api, Endpoint endpoint) {
		Pattern pattern = Pattern.compile("\\{.+?\\}");
		Matcher matcher = pattern.matcher(endpoint.getEndpoint().replace("?", "\\?")); // TODO better escaping
		String regex = ".*" + api.getBaseUrl() + matcher.replaceAll(".+?");
		return Pattern.compile(regex);
	}
	
	public MockEndpoint getEndpoint(String path) {
		for (Entry<Pattern, MockEndpoint> entry : repo.entrySet()) {
			if (entry.getKey().matcher(path).matches()) {
				return entry.getValue();
			}
		}
		return null;
	}
}