package com.github.samblake.rexy.module.template;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ParameterTemplateProcessorTest {
	
	@Test
	public void testSubstitute() {
		MockRequest request = new MockRequest().withParameter("query", "substitution");
		TemplateProcessor processor = new ParameterTemplateProcessor("param");
		String substitute = processor.substitute(request, "param:query");
		assertThat(substitute, is("substitution"));
	}
	
	@Test
	public void testSubstituteNoMatch() {
		MockRequest request = new MockRequest();
		TemplateProcessor processor = new ParameterTemplateProcessor("param");
		String substitute = processor.substitute(request, "param:query");
		assertThat(substitute, is(""));
	}
	
}