package com.github.samblake.rexy.module.jmx.mock;

import com.github.samblake.rexy.module.jmx.JmxRegistry;
import com.github.samblake.rexy.module.jmx.ObjectNameBuilder;
import com.github.samblake.rexy.config.model.Endpoint;
import com.github.samblake.rexy.config.model.Response;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Map;

import static com.github.samblake.rexy.utils.Bodies.findBody;
import static org.apache.commons.lang3.StringUtils.isEmpty;

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
			MockResponse mockResponse = createMockResponse(mockEndpoint, response);
			registerMBean(endpoint.getApi().getName(), endpoint.getName(), mockResponse, name);
		}
		return mockEndpoint;
	}
	
	private MockResponse createMockResponse(MockEndpoint mockEndpoint, Response response) {
		int httpStatus = response.getHttpStatus();
		Map<String, String> headers = response.getHeaders();
		String body = findBody(response); // FIXME move this out to startup
		return new MockResponse(mockEndpoint, httpStatus, headers, body, interceptOnSet);
	}
	
	@Override
	protected MockEndpoint createMBean(Endpoint endpoint) {
		Response defaultResponse = endpoint.getResponses().iterator().next();
		
		String contentType = endpoint.getApi().getContentType();
		int httpStatus = defaultResponse.getHttpStatus();
		Map<String, String> headers = defaultResponse.getHeaders();
		String body = findBody(defaultResponse); // FIXME move this out to startup
		boolean intercept = endpoint.getApi().getProxy() == null;
		
		return new MockEndpoint(contentType, httpStatus, headers, body, intercept);
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