package com.github.samblake.rexy.module.wexy.actions.endpoint;

import com.github.samblake.rexy.module.wexy.model.input.Input;

import javax.management.MBeanInfo;
import javax.management.ObjectInstance;
import java.util.List;

public class Module {
	
	private final ObjectInstance objectInstance;
	private final MBeanInfo mBeanInfo;
	private final List<Input> attributes;
	private final String action;
	
	public Module(ObjectInstance objectInstance, MBeanInfo mBeanInfo, List<Input> attributes, String action) {
		this.objectInstance = objectInstance;
		this.mBeanInfo = mBeanInfo;
		this.attributes = attributes;
		this.action = action;
	}
	
	public List<Input> getInputs() {
		return attributes;
	}
	
	public String getAction() {
		return action;
	}
}