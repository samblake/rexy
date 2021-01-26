package com.github.samblake.rexy.module.jmx.delay;

/**
 * The delay endpoint MBean for the {@link DelayModule}.
 */
public interface DelayEndpointMBean {
	
	/**
	 * Gets the delay in seconds.
	 */
	int getDelay();
	
	/**
	 * Sets the delay in seconds.
	 */
	void setDelay(int delay);
	
}