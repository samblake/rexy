package com.github.samblake.rexy.module.wexy.model.input;

public class TextInput extends ValueInput<String> {
	
	public TextInput(String label, String name, String value) {
		super("text", label, name, value);
	}
	
}