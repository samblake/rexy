package com.github.samblake.rexy.module.template;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class XpathTemplateProcessorTest {
	
	@Test
	public void testSubstitute() {
		MockRequest request = new MockRequest().withBody("<a><b/><c x=\"param\">text</c></a>");
		TemplateProcessor processor = new XpathTemplateProcessor("xpath");
		String substitute = processor.substitute(api, request, "xpath://b");
		assertThat(substitute, is("<b/>"));
	}
	
	@Test
	public void testSubstituteFull() {
		MockRequest request = new MockRequest().withBody("<a><b/><c x=\"param\">text</c></a>");
		TemplateProcessor processor = new XpathTemplateProcessor("xpath");
		String substitute = processor.substitute(api, request, "xpath:/a");
		assertThat(substitute, is("<a><b/><c x=\"param\">text</c></a>"));
	}
	
	@Test
	public void testSubstituteAttribute() {
		MockRequest request = new MockRequest().withBody("<a><b/><c x=\"param\">text</c></a>");
		TemplateProcessor processor = new XpathTemplateProcessor("xpath");
		String substitute = processor.substitute(api, request, "xpath://c/@x");
		assertThat(substitute, is("param"));
	}
	
	@Test
	public void testSubstituteText() {
		MockRequest request = new MockRequest().withBody("<a><b/><c x=\"param\">text</c></a>");
		TemplateProcessor processor = new XpathTemplateProcessor("xpath");
		String substitute = processor.substitute(api, request, "xpath://c/text()");
		assertThat(substitute, is("text"));
	}
	
	@Test
	public void testSubstituteNoMatch() {
		MockRequest request = new MockRequest().withBody("<a><b/><c x=\"param\">text</c></a>");
		TemplateProcessor processor = new XpathTemplateProcessor("xpath");
		String substitute = processor.substitute(api, request, "xpath://d");
		assertThat(substitute, is(""));
	}
	
}