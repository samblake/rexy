package com.github.samblake.rexy.module.wexy.mbean;

import com.github.samblake.rexy.module.wexy.builders.Builder;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public abstract class MBeanQueryBuilder implements Builder<ObjectName> {
	
	protected abstract StringBuilder getName();

	@Override
	public ObjectName build() {
		try {
			return buildObjectName();
		}
		catch (MalformedObjectNameException e) {
			throw new RuntimeException("Invalid object name", e);
		}
	}
	
	protected ObjectName buildObjectName() throws MalformedObjectNameException {
		return new ObjectName(getName().append('*').toString());
	}
	
}