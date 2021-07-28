package com.github.samblake.rexy.module.wexy.model.input;

import static org.apache.commons.lang3.StringUtils.isBlank;

public abstract class FormattedInput extends ValueInput<String> {
	
	public FormattedInput(String type, String label, String name, String value) {
		super(type, label, name, value);
	}
	
	public String getPrettyValue() {
		return isBlank(getValue()) ? getValue() : prettyPrint(getValue());
	}
	
	protected abstract String prettyPrint(String value);
	
}