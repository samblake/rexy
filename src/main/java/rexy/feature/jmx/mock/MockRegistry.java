package rexy.feature.jmx.mock;

import rexy.config.model.Api;
import rexy.config.model.Endpoint;
import rexy.config.model.Response;
import rexy.feature.jmx.JmxRegistry;

import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.OperationsException;
import java.lang.management.ManagementFactory;

public final class MockRegistry extends JmxRegistry<MockEndpoint> {
	
	private static final MockRegistry INSTANCE = new MockRegistry();
	
	private MockRegistry() {
	}
	
	public static MockRegistry getInstance() {
		return INSTANCE;
	}
	
	@Override
	public MockEndpoint addEndpoint(Api api, Endpoint endpoint) throws OperationsException, MBeanRegistrationException {
		MockEndpoint mockEndpoint = super.addEndpoint(api, endpoint);
		int i = 0;
		for (Response response : endpoint.getResponses()) {
			MockResponse mockResponse = new MockResponse(mockEndpoint, response);
			registerMBean(api.getName(), endpoint.getName(), mockResponse, i++);
		}
		return mockEndpoint;
	}
	
	@Override
	protected MockEndpoint createMBean(Api api, Endpoint endpoint) {
		Response defaultResponse = endpoint.getResponses().iterator().next();
		return new MockEndpoint(api.getContentType(), defaultResponse);
	}
	
	private void registerMBean(String type, String name, MockResponse response, int i)
			throws OperationsException, MBeanRegistrationException {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		ObjectName objectName = new ObjectName("Rexy:type=" + type + ",scope=" + name + ",name=preset-" + i);
		server.registerMBean(response, objectName);
	}
}