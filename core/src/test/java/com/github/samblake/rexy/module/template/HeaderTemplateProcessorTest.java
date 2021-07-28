package com.github.samblake.rexy.module.template;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class HeaderTemplateProcessorTest {
	
	@Test
	public void testSubstitute() {
		MockRequest request = new MockRequest().withHeader("X-Test-Header", "substitution");
		TemplateProcessor processor = new HeaderTemplateProcessor("header");
		String substitute = processor.substitute(request, "header:X-Test-Header");
		assertThat(substitute, is("substitution"));
	}
	
	@Test
	public void testSubstituteNoMatch() {
		MockRequest request = new MockRequest();
		TemplateProcessor processor = new HeaderTemplateProcessor("header");
		String substitute = processor.substitute(request, "header:X-Test-Header");
		assertThat(substitute, is(""));
	}
	
}