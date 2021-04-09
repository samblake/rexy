package com.github.samblake.rexy.module.wexy.model.input;

// TODO for enums
public class SelectInput extends ValueInput<String> {
	
	public SelectInput(String label, String name, String value) {
		super("select", label, name, value);
	}

	public String[] getOptions() {
		return new String[] {};
	}

}