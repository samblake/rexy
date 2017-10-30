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

public class JmxRegistry {
	
	private static final JmxRegistry INSTANCE = new JmxRegistry();

	private final Map<String, MockEndpoint> repo = new HashMap<>();

	private JmxRegistry() {
	}
	
	public static JmxRegistry getInstance() {
		return INSTANCE;
	}

	public void addEndpoint(Api api, Endpoint endpoint) throws OperationsException, MBeanRegistrationException {
		Response response = endpoint.getResponses().iterator().next();
		MockEndpoint mockEndpoint = new MockEndpoint(api.getContentType(), response.getHttpStatus(), response.getBody().asText());

		String name = api.getName() + "/" + endpoint.getName();
		registerMBean(name, mockEndpoint);
		repo.put(name, mockEndpoint);
	}

	public MockEndpoint getEndpoint(Endpoint endpoint) {
		return repo.get(endpoint.getEndpoint());
	}
	
	private void registerMBean(String name, MockEndpoint endpoint) throws OperationsException, MBeanRegistrationException {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		ObjectName objectName = new ObjectName(name + ":type=MockResponse");
		server.registerMBean(endpoint, objectName);
	}

	public MockEndpoint getEndpoint(String path) {
		for (String name : repo.keySet()) {
			if (path.endsWith(name)) { // TODO better matching
				return repo.get(name);
			}
		}
		return null;
	}
}