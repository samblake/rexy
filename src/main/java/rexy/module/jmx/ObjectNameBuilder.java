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
		boolean initial = true;
		StringBuilder objectName = new StringBuilder(base).append(":");
		
		if (type != null) {
			objectName.append("type=").append(type);
			initial = false;
		}
		
		if (scope != null) {
			if (!initial) {
				objectName.append(",");
			}
			objectName.append("scope=").append(scope);
			initial = false;
		}
		
		if (!initial) {
			objectName.append(",");
		}
		objectName.append("objectName=").append(name);
		
		return objectName.toString();
	}
}
