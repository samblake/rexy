package com.github.samblake.rexy.module.template;

import com.github.samblake.rexy.Rexy.RexyDetails;
import com.github.samblake.rexy.config.model.Api;
import com.github.samblake.rexy.http.response.RexyResponse;
import com.github.samblake.rexy.mocks.MockResponse;
import org.junit.Test;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TemplateModuleTest {
	
	@Test
	public void test() throws IOException {
		test("<a><b/><c x=\"param\">text</c></a>", "<xyz>${xpath://c}</xyz>", "<xyz><c x=\"param\">text</c></xyz>");
	}
	
	@Test
	public void testNoMatch() throws IOException {
		TemplateModuleTest.this.test("<a/>", "<xyz>${xpath://c}</xyz>", "<xyz></xyz>");
	}
	
	@Test
	public void testNoTemplate() throws IOException {
		TemplateModuleTest.this.test("<a/>", "<xyz>${xyz:123}</xyz>", "<xyz>${xyz:123}</xyz>");
	}
	
	@Test
	public void testFailure() throws IOException {
		TemplateModuleTest.this.test("", "<xyz>${xpath://c}</xyz>", "<xyz>${xpath://c}</xyz>");
	}
	
	private void test(String requestBody, String responseBody, String expectedResult) throws IOException {
		MockRequest request = new MockRequest().withBody(requestBody);
		test(request, responseBody, expectedResult);
	}
	
	private void test(MockRequest request, String responseBody, String expectedResult) throws IOException {
		TemplateModule module = new TemplateModule();
		module.init(new RexyDetails(80, "/"), null);
		
		MockResponse response = new MockResponse().withBody(responseBody);
		
		RexyResponse processedResponse = module.processResponse(new Api(), request, response);
		
		String body = new String(processedResponse.getBody().readAllBytes(), UTF_8);
		assertThat(body, is(expectedResult));
	}
	
}