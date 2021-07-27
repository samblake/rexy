package com.github.samblake.rexy.module.template;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RegexTemplateProcessorTest {
	
	@Test
	public void testSubstitute() {
		MockRequest request = new MockRequest().withBody("This is a [substitution] body");
		TemplateProcessor processor = new RegexTemplateProcessor("regex");
		String substitute = processor.substitute(api, request, "regex:\\[.*\\]");
		assertThat(substitute, is("[substitution]"));
	}
	
	@Test
	public void testSubstituteGroup() {
		MockRequest request = new MockRequest().withBody("This is a [substitution] body");
		TemplateProcessor processor = new RegexTemplateProcessor("regex");
		String substitute = processor.substitute(api, request, "regex:\\[(.*)\\]");
		assertThat(substitute, is("substitution"));
	}
	
	@Test
	public void testSubstituteNoMatch() {
		MockRequest request = new MockRequest().withBody("This is a [substitution] body");
		TemplateProcessor processor = new RegexTemplateProcessor("regex");
		String substitute = processor.substitute(api, request, "regex:[0-9]*");
		assertThat(substitute, is(""));
	}
	
}