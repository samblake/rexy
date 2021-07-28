package com.github.samblake.rexy.module.template;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class JsonPathTemplateProcessorTest {
	
	@Test
	public void testSubstitute() {
		MockRequest request = new MockRequest().withBody("{ 'a': 1, 'b': { 'c': 2 } }");
		TemplateProcessor processor = new JsonPathTemplateProcessor("jsonpath");
		String substitute = processor.substitute(request, "jsonpath:$.b.c");
		assertThat(substitute, is("2"));
	}
	
	@Test
	public void testSubstituteFull() {
		MockRequest request = new MockRequest().withBody("{ 'a': 1, 'b': { 'c': 2 } }");
		TemplateProcessor processor = new JsonPathTemplateProcessor("jsonpath");
		String substitute = processor.substitute(request, "jsonpath:$");
		assertThat(substitute, is("{a=1, b={c=2}}"));
	}
	
	@Test
	public void testSubstituteNoMatch() {
		MockRequest request = new MockRequest().withBody("{ 'a': 1, 'b': { 'c': 2 } }");
		TemplateProcessor processor = new JsonPathTemplateProcessor("jsonpath");
		String substitute = processor.substitute(request, "jsonpath:$.d");
		assertThat(substitute, is(""));
	}
	
}