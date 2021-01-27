package com.github.samblake.rexy.module.wexy.mbean.converters;

public interface TypeConverter<T> {
	
	boolean handles(String type);
	
	T convert(String value, String type);
	
}
