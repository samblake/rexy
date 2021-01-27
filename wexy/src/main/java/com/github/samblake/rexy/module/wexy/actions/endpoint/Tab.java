package com.github.samblake.rexy.module.wexy.actions.endpoint;

public class Tab<T> {
	
	private String id;
	private String name;
	private T model;
	private boolean active;
	
	public Tab(String name, T model) {
		this(name, name, model);
	}
	
	public Tab(String id, String name, T model) {
		this.id = id;
		this.name = name;
		this.model = model;
		this.active = false;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public T getModel() {
		return model;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive() {
		this.active = true;
	}
	
}