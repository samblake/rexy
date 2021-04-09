package com.github.samblake.rexy.module.wexy.actions.endpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.github.samblake.rexy.module.wexy.model.input.CheckboxInput;
import com.github.samblake.rexy.module.wexy.model.input.Input;
import com.github.samblake.rexy.module.wexy.model.input.JsonInput;
import com.github.samblake.rexy.module.wexy.model.input.NumberInput;
import com.github.samblake.rexy.module.wexy.model.input.TagInput;
import com.github.samblake.rexy.module.wexy.model.input.TextInput;
import com.github.samblake.rexy.module.wexy.model.input.XmlInput;

import javax.management.MBeanAttributeInfo;
import javax.management.ObjectInstance;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Character.toUpperCase;
import static java.lang.String.join;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;

public class InputFactory {
	
	public static Input createInput(ObjectInstance objectInstance,
			MBeanAttributeInfo attributeInfo, String contentType, Object value) {
		
		String beanName = objectInstance.getObjectName().getKeyProperty("name");
		String attributeName = attributeInfo.getName();
		
		String label = join(" ", splitByCharacterTypeCamelCase(attributeName));
		label = toUpperCase(label.charAt(0)) + label.substring(1);
		
		Input input = createInput(attributeInfo, beanName, attributeName, label, value, contentType);
		
		if (!attributeInfo.isWritable()) {
			input.setDisabled();
		}
		
		return input;
	}
	
	private static Input createInput(MBeanAttributeInfo attributeInfo, String beanName,
			String attributeName, String label, Object value, String contentType) {
		
		if (beanName.equals("mock") && attributeName.equals("Body")) {
			if (contentType.toLowerCase().contains("json")) {
				return new JsonInput(label, attributeName, value == null ? "" : value.toString());
			}
			if (contentType.toLowerCase().contains("xml")) {
				return new XmlInput(label, attributeName, value == null ? "" : value.toString());
			}
			return new TextInput(label, attributeName, (String)value);
		}
		
		String type = attributeInfo.getType();
		
		switch (type) {
			case "boolean":
				return new CheckboxInput(label, attributeName, parseBoolean(value.toString()));
			
			case "short":
				return new NumberInput(label, attributeName, BigDecimal.valueOf((Short)value));
			case "int":
				return new NumberInput(label, attributeName, BigDecimal.valueOf((Integer)value));
			case "long":
				return new NumberInput(label, attributeName, BigDecimal.valueOf((Long)value));
			case "float":
				return new NumberInput(label, attributeName, BigDecimal.valueOf((Float)value));
			case "double":
				return new NumberInput(label, attributeName, BigDecimal.valueOf((Double)value));
				
			case "btye":
			case "char":
				return new TextInput(label, attributeName, value.toString());
		}
		
		if (value instanceof String) {
			return new TextInput(label, attributeName, (String)value);
		}
		
		if (value instanceof BigDecimal) {
			return new NumberInput(label, attributeName, (BigDecimal)value);
		}
		
		if (value instanceof BigInteger) {
			return new NumberInput(label, attributeName, new BigDecimal((BigInteger)value));
		}
		
		if (value instanceof Collection) {
			return new TagInput(label, attributeName, value.toString());
		}

		if (value instanceof Map) {
			String stringValue = ((Map<?, ?>) value).entrySet().stream()
					.map(e -> e.getKey() + ": " + e.getValue())
					.collect(joining(","));
			return new TagInput(label, attributeName, stringValue);
		}
		
		throw new RuntimeException("Unknown type: " + type);
	}
	
}