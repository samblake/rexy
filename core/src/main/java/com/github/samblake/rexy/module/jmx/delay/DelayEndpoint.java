package com.github.samblake.rexy.module.jmx.delay;

/**
 * The delay endpoint implementation for the {@link DelayModule}.
 */
public class DelayEndpoint implements DelayEndpointMBean {
	
	private int delay;
	
	public DelayEndpoint() {
		this.delay = 0;
	}
	
	@Override
	public int getDelay() {
		return delay;
	}
	
	@Override
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
}