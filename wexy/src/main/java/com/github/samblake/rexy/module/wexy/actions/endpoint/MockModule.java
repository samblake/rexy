package com.github.samblake.rexy.module.wexy.actions.endpoint;

import com.github.samblake.rexy.module.wexy.model.input.Input;

import javax.management.MBeanInfo;
import javax.management.ObjectInstance;
import java.util.List;

public class MockModule extends Module {
	
	private final List<PresetLink> presets;
	
	public MockModule(ObjectInstance objectInstance, MBeanInfo mBeanInfo,
	                  List<Input> attributes, String action, List<PresetLink> presets) {
		super(objectInstance, mBeanInfo, attributes, action);
		this.presets = presets;
	}
	
	public List<PresetLink> getPresets() {
		return presets;
	}
	
}