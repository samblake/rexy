package com.github.samblake.rexy.module.template;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.samblake.rexy.Rexy.RexyDetails;
import com.github.samblake.rexy.config.model.Api;
import com.github.samblake.rexy.http.request.RexyRequest;
import com.github.samblake.rexy.http.response.RexyResponse;
import com.github.samblake.rexy.http.response.RexyResponseDelegate;
import com.github.samblake.rexy.module.ModuleAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A module applies substitutions to a response.
 *
 * <pre><code>{@code
 * "template": {
 *   "enabled": true,
 * }
 * }</code></pre>
 */
public class TemplateModule extends ModuleAdapter {
	
	private static final Pattern PATTERN = Pattern.compile("\\$\\{(.+?)}");
	
	public static final String PARAM = "param";
	public static final String HEADER = "header";
	public static final String XPATH = "xpath";
	public static final String JSONPATH = "jsonpath";
	public static final String REGEX = "regex";
	
	private final TemplateProcessorFactory processorFactory = new TemplateProcessorFactory();
	
	@Override
	public void init(RexyDetails rexyDetails, JsonNode config) {
		processorFactory.addProcessor(new ParameterTemplateProcessor(PARAM));
		processorFactory.addProcessor(new HeaderTemplateProcessor(HEADER));
		processorFactory.addProcessor(new XpathTemplateProcessor(XPATH));
		processorFactory.addProcessor(new JsonPathTemplateProcessor(JSONPATH));
		processorFactory.addProcessor(new RegexTemplateProcessor(REGEX));
	}
	
	@Override
	public RexyResponse processResponse(Api api, RexyRequest request, RexyResponse response) {
		try {
			String origBody = new String(response.getBody().readAllBytes(), UTF_8);
			Matcher matcher = PATTERN.matcher(origBody);

			String updatedBody = processTemplates(request, matcher);
			return new TemplateResponse(response, updatedBody);
		}
		catch (IOException e) {
			throw new RuntimeException("Response could not be read");
		}
	}
	
	private String processTemplates(RexyRequest request, Matcher matcher) {
		StringBuilder body = new StringBuilder();
		
		while (matcher.find()) {
			String substitution = processorFactory.substitute(request, matcher.group(1));
			matcher.appendReplacement(body, substitution);
		}
		
		matcher.appendTail(body);
		
		return body.toString();
	}
	
	private static final class TemplateResponse extends RexyResponseDelegate {
		
		private final byte[] body;
		
		protected TemplateResponse(RexyResponse delegate, String body) {
			super(delegate);
			this.body = body.getBytes(UTF_8);
		}
		
		@Override
		public InputStream getBody() {
			return new ByteArrayInputStream(body);
		}
		
		@Override
		public int getResponseLength() {
			return body.length;
		}
		
	}
	
}