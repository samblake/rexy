package rexy.module.jmx.delay;

import rexy.config.model.Endpoint;
import rexy.module.jmx.JmxRegistry;

/**
 * A registry for {@link DelayEndpoint delay endpoints}.
 */
public final class DelayRegistry extends JmxRegistry<DelayEndpoint> {
	
	@Override
	protected DelayEndpoint createMBean(Endpoint endpoint) {
		return new DelayEndpoint();
	}

	@Override
	protected String getMBeanName() {
		return "delay";
	}
	
}