package com.github.samblake.rexy.module.wexy.model.input;

public abstract class ValueInput<T> extends Input {
	
	private final T value;
	
	protected ValueInput(String type, String name, T value) {
		this(type, name, name, value);
	}
	
	protected ValueInput(String type, String label, String name, T value) {
		super(type, label, name);
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
}