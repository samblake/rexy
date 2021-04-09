package com.github.samblake.rexy.module.wexy.mbean;

import com.github.samblake.rexy.config.model.Api;
import com.github.samblake.rexy.config.model.Endpoint;
import com.github.samblake.rexy.module.wexy.mbean.converters.BasicTypeConverter;
import com.github.samblake.rexy.module.wexy.mbean.converters.MapConverter;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Set;

public class MBeanRepo {
	
	private final MBeanServer server;
	private final BasicTypeConverter basicTypeConverter;
	
	public MBeanRepo() {
		this.server = ManagementFactory.getPlatformMBeanServer();
		this.basicTypeConverter = new BasicTypeConverter(new MapConverter());
	}
	
	public Set<ObjectInstance> search(MBeanQueryBuilder queryBuilder) {
		return server.queryMBeans(queryBuilder.build(), null);
	}
	
	public MBeanInfo getInfo(ObjectInstance objectInstance) {
		try {
			return server.getMBeanInfo(objectInstance.getObjectName());
		}
		catch (JMException e) {
			throw new RuntimeException("Could not get MBean info", e);
		}
	}
	
	public Object getAttributeValue(ObjectInstance oi, MBeanAttributeInfo mbai) {
		try {
			return server.getAttribute(oi.getObjectName(), mbai.getName());
		}
		catch (JMException e) {
			throw new RuntimeException("Could not get attriute value", e);
		}
	}
	
	public Set<ObjectInstance> findForEndpoint(Api api, Endpoint endpoint) {
		MBeanQueryBuilder query = new RexyQueryBuilder().withApi(api.getName()).withEndpoint(endpoint.getName());
		return search(query);
	}
	
	public void applyPreset(Endpoint endpoint, String presetName) throws JMException {
		MBeanQueryBuilder query = new RexyQueryBuilder()
				.withApi(endpoint.getApi().getName())
				.withEndpoint(endpoint.getName())
				.withComponent("preset")
				.withName(presetName);
		
		server.invoke(query.build(), "set", null, null);
	}
	
	public void updateValues(Endpoint endpoint, String moduleName, Map<String, String> params) throws JMException {
		ObjectName objectName = new RexyQueryBuilder()
				.withApi(endpoint.getApi().getName())
				.withEndpoint(endpoint.getName())
				.withName(moduleName)
				.build();
		
		MBeanInfo info = server.getMBeanInfo(objectName);
		AttributeList attributes = new AttributeList(info.getAttributes().length);
		for (MBeanAttributeInfo attributeInfo : info.getAttributes()) {
			String param = params.getOrDefault(attributeInfo.getName(), "").trim();
			Object value = basicTypeConverter.convert(param, attributeInfo.getType());
			attributes.add(new Attribute(attributeInfo.getName(), value));
		}
		
		server.setAttributes(objectName, attributes);
	}
	
}