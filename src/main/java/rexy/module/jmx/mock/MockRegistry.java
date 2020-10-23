package rexy.module.jmx.mock;

import rexy.config.model.Endpoint;
import rexy.config.model.Response;
import rexy.module.jmx.JmxRegistry;
import rexy.module.jmx.ObjectNameBuilder;

import javax.management.JMException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.OperationsException;
import java.lang.management.ManagementFactory;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * A registry for {@link MockEndpoint mock endpoints}.
 */
public final class MockRegistry extends JmxRegistry<MockEndpoint> {
	
	private final boolean interceptOnSet;
	
	public MockRegistry(boolean interceptOnSet) {
		this.interceptOnSet = interceptOnSet;
	}
	
	@Override
	public MockEndpoint addEndpoint(Endpoint endpoint) throws JMException {
		MockEndpoint mockEndpoint = super.addEndpoint(endpoint);
		int i = 0;
		for (Response response : endpoint.getResponses()) {
			String name = isEmpty(response.getName()) ? Integer.toString(i++) : response.getName();
			MockResponse mockResponse = new MockResponse(mockEndpoint, response, interceptOnSet);
			registerMBean(endpoint.getApi().getName(), endpoint.getName(), mockResponse, name);
		}
		return mockEndpoint;
	}
	
	@Override
	protected MockEndpoint createMBean(Endpoint endpoint) {
		Response defaultResponse = endpoint.getResponses().iterator().next();
		String contentType = endpoint.getApi().getContentType();
		boolean intercept = endpoint.getApi().getProxy() == null;
		return new MockEndpoint(contentType, defaultResponse, intercept);
	}
	
	private void registerMBean(String type, String endpoint, MockResponse response, String name)
			throws OperationsException, MBeanRegistrationException {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		
		ObjectName objectName = new ObjectNameBuilder()
				.withApi(type).withEndpoint(endpoint).withComponent("preset").withName(name).build();
		server.registerMBean(response, objectName);
	}
	
	@Override
	protected String getMBeanName() {
		return "mock";
	}
	
}