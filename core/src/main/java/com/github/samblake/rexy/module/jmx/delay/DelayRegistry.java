package com.github.samblake.rexy.module.jmx.delay;

import com.github.samblake.rexy.module.jmx.JmxRegistry;
import com.github.samblake.rexy.config.model.Endpoint;

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