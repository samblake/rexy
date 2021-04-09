package com.github.samblake.rexy.module.wexy.model.input;

public class CheckboxInput extends ValueInput<Boolean> {
	
	public CheckboxInput(String label, String name, boolean value) {
		super("checkbox", label, name, value);
	}
	
}