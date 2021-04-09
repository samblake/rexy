package com.github.samblake.rexy.module.wexy.mbean.converters;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Byte.parseByte;
import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.Short.parseShort;

public class BasicTypeConverter implements TypeConverter<Object> {
	
	private final TypeConverter<?>[] converters;
	
	public BasicTypeConverter(TypeConverter<?>... converters) {
		this.converters = converters;
	}
	
	@Override
	public boolean handles(String type) {
		return true;
	}
	
	@Override
	public Object convert(String value, String type) {
		// Handle Strings
		if (type.equals(String.class.getName())) {
			return value;
		}
		
		// Handle primatives
		switch (type) {
			case "boolean": return parseBoolean(value);
			case "byte": return parseByte(value);
			case "short": return parseShort(value);
			case "int": return parseInt(value);
			case "long": return parseLong(value);
			case "char": return value.charAt(0);
			case "float": return parseFloat(value);
			case "double": return parseDouble(value);
		}
		
		// Handle any other registered types
		for (TypeConverter<?> converter : converters) {
			if (converter.handles(type)) {
				return converter.convert(value, type);
			}
		}
		
		throw new RuntimeException("No handler for type: " + type);
	}
	
}
