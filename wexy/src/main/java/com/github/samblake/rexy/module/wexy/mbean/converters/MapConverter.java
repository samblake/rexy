package com.github.samblake.rexy.module.wexy.mbean.converters;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

public class MapConverter implements TypeConverter<Map<Object, Object>> {
	
	private static final Pattern TAG_SEPERATOR = Pattern.compile(",");
	private static final Pattern ENTRY_PATTERN = Pattern.compile("\\s*(.+?)\\s*:\\s*(.+?)\\s*");
	
	@Override
	public boolean handles(String type) {
		try {
			return Class.forName(type).isAssignableFrom(Map.class);
		}
		catch (ClassNotFoundException e) {
			return false;
		}
	}
	
	@Override
	public Map<Object, Object> convert(String value, String type) {
		return value == null ? emptyMap() : TAG_SEPERATOR.splitAsStream(value)
			.map(ENTRY_PATTERN::matcher)
			.filter(Matcher::matches)
			.collect(toMap(matcher -> matcher.group(1), matcher -> matcher.group(2)));
	}

}