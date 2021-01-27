package com.github.samblake.rexy.module.wexy.actions.endpoint;

import javax.management.ObjectInstance;

public class EndpointObjectInstances {
	
	private EndpointObjectInstances() {
	}
	
	public static boolean isPreset(ObjectInstance objectInstance) {
		return "preset".equals(objectInstance.getObjectName().getKeyProperty("component"));
	}
	
	public static boolean isModule(ObjectInstance objectInstance) {
		return objectInstance.getObjectName().getKeyProperty("component") == null;
	}
	
	public static boolean isMock(ObjectInstance objectInstance) {
		return "mock".equals(objectInstance.getObjectName().getKeyProperty("name"));
	}
	
}