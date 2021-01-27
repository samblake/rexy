package com.github.samblake.rexy.module.wexy.mbean.converters;

import org.junit.Test;

import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class MapConverterTest {
	
	@Test
	public void testEmpty() {
		assertThat(convert(null)).isEmpty();
		assertThat(convert("")).isEmpty();
		assertThat(convert("   ")).isEmpty();
	}
	
	@Test
	public void testSingle() {
		assertThat(convert("abc: 123")).hasSize(1);
		assertThat(convert("abc: 123")).containsEntry("abc", "123");
		assertThat(convert(" abc: 123")).containsEntry("abc", "123");
		assertThat(convert(" abc :123")).containsEntry("abc", "123");
		assertThat(convert("abc :123 ")).containsEntry("abc", "123");
	}
	
	@Test
	public void testTwo() {
		Map<Object, Object> twoValues = convert("abc: 123,def: 456");
		assertThat(twoValues).hasSize(2);
		assertThat(twoValues).containsEntry("abc", "123");
		assertThat(twoValues).containsEntry("def", "456");
	}
	
	@Test
	public void testInvalid() {
		assertThat(convert("bad")).isEmpty();
		
		Map<Object, Object> badValues = convert("abc: 123, bad, def: 456, bad");
		assertThat(badValues).hasSize(2);
		assertThat(badValues).containsEntry("abc", "123");
		assertThat(badValues).containsEntry("def", "456");
	}
	
	private Map<Object, Object> convert(String value) {
		return new MapConverter().convert(value, null);
	}
	
}