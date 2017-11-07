package rexy.feature.jmx.delay;

import rexy.config.model.Api;
import rexy.config.model.Endpoint;
import rexy.feature.jmx.JmxRegistry;

public final class DelayRegistry extends JmxRegistry<DelayEndpoint> {
	
	@Override
	protected DelayEndpoint createMBean(Api api, Endpoint endpoint) {
		return new DelayEndpoint();
	}

	@Override
	protected String getMBeanName() {
		return "delay";
	}
}