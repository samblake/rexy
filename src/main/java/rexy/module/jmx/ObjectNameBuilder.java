package rexy.module.jmx;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * Builds an object name for an MBean. At least the base value and name must be supplied.
 * Both <i>type</i> and <i>scope</i> are optional.
 */
public class ObjectNameBuilder {
	
	private final String base;
	private String type;
	private String scope;
	private String component;
	private String name;
	
	public ObjectNameBuilder() {
		this("Rexy");
	}
	
	public ObjectNameBuilder(String base) {
		this.base = base;
	}
	
	public ObjectNameBuilder withType(String type) {
		this.type = type;
		return this;
	}
	
	public ObjectNameBuilder withScope(String scope) {
		this.scope = scope;
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
		
		if (type != null) {
			objectName.append("type=").append(type);
		}
		
		if (scope != null) {
			if (objectName.length() > initialLength) {
				objectName.append(',');
			}
			objectName.append("scope=").append(scope);
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
