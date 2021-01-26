package com.github.samblake.rexy.module.jmx;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * Builds an object name for an MBean. At least the base value and name must be supplied.
 * Both <i>api</i> and <i>endpoint</i> are optional.
 */
public class ObjectNameBuilder {
	
	private final String base;
	private String api;
	private String endpoint;
	private String component;
	private String name;
	
	public ObjectNameBuilder() {
		this("Rexy");
	}
	
	public ObjectNameBuilder(String base) {
		this.base = base;
	}
	
	public ObjectNameBuilder withApi(String api) {
		this.api = api;
		return this;
	}
	
	public ObjectNameBuilder withEndpoint(String endpoint) {
		this.endpoint = endpoint;
		return this;
	}
	
	public ObjectNameBuilder withComponent(String component) {
		this.component = component;
		return this;
	}
	
	public ObjectNameBuilder withName(String name) {
		this.name = name;
		return this;
	}
	
	public ObjectName build() throws MalformedObjectNameException {
		validate();
		String name = buildName();
		return new ObjectName(name);
	}
	
	private void validate() throws MalformedObjectNameException {
		if (base == null) {
			throw new MalformedObjectNameException("A base value must be supplied");
		}
		
		if (name == null) {
			throw new MalformedObjectNameException("A name value must be supplied");
		}
	}
	
	private String buildName() {
		StringBuilder objectName = new StringBuilder(base).append(':');
		int initialLength = objectName.length();
		
		if (api != null) {
			objectName.append("api=").append(api);
		}
		
		if (endpoint != null) {
			if (objectName.length() > initialLength) {
				objectName.append(',');
			}
			objectName.append("endpoint=").append(endpoint);
		}
		
		if (component != null) {
			if (objectName.length() > initialLength) {
				objectName.append(',');
			}
			objectName.append("component=").append(component);
		}
		
		if (objectName.length() > initialLength) {
			objectName.append(',');
		}
		objectName.append("name=").append(name);
		
		return objectName.toString();
	}
}
