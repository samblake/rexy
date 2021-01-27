package com.github.samblake.rexy.module.wexy.model.input;

public abstract class Input {
	
	private final String type;
	private final String label;
	private final String name;
	
	private boolean disabled;
	
	protected Input(String type, String label, String name) {
		this.type = type;
		this.label = label;
		this.name = name;
		
		this.disabled = false;
	}
	
	public String getType() {
		return type;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getName() {
		return name;
	}
	
	public void setDisabled() {
		disabled = true;
	}
	
	public boolean isDisabled() {
		return disabled;
	}
	
}