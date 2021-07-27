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
 * <p>A module that applies substitutions to a response body.</p>
 *
 * <pre><code>{@code
 * "template": {
 *   "enabled": true,
 * }
 * }</code></pre>
 *
 * <p>There are six different types of substitution: param, header, xpath, jsonpath, regex and request. Each can be
 * inserted into a response body with the following syntax:
 *
 * <pre><code>{@code
 * ${[name]:[expression]}
 * }</code></pre>
 *
 * <p><b>Param</b></p>
 *
 * <p>The {@code param} substitution's expression should be the name of a request parameter. The template will be
 * substituted for the value of the specified parameter. If there are multiple values for the specified name the
 * first value found will be used. An example template is:</p>
 *
 * <pre><code>{@code
 * ${param:id}
 * }</code></pre>
 *
 * <p><b>Header</b></p>
 *
 * <p>The {@code header} substitution's expression should be the name of a request header. The template will be
 * substituted for the value of the specified header. If there are multiple values for the specified name the
 * first value found will be used. An example template is:</p>
 *
 * <pre><code>{@code
 * ${header:user-agent}
 * }</code></pre>
 *
 * <p><b>XPath</b></p>
 *
 * <p>The {@code xpath} substitution's expression should be an
 * <a href="https://www.w3.org/TR/1999/REC-xpath-19991116/">XPath</a> expression. The template will be substituted
 * for the result of evaluating the XPath expression against the request body. As such it should only be used
 * when the entire body is valid XML. An example template is:</p>
 *
 * <pre><code>{@code
 * ${xpath:/bookstore/book[1]/title}
 * }</code></pre>
 *
 * <p><b>JSONPath</b></p>
 *
 * <p>The {@code jsonpath} substitution's expression should be a
 * <a href="https://tools.ietf.org/id/draft-goessner-dispatch-jsonpath-00.html/">JSONPath</a> expression. The
 * template will be substituted for the result of evaluating the JSONPath expression against the request body. As such
 * it should only be used when the entire body is valid JSON. An example template is:</p>
 *
 * <pre><code>{@code
 * ${jsonpath:$['bookstore']['book'][1]['title']}
 * }</code></pre>
 *
 * <p><b>Regex</b></p>
 *
 * <p>The {@code regex} substitution's expression should be a regular expression. The template will be substituted for
 * first match found when evaluating the regular expression against the request body. If the expression contains group
 * the first group will be supplied as the result rather than the entire match. An example template is:</p>
 *
 * <pre><code>{@code
 * ${regex:id=(.*?)(&|$)}
 * }</code></pre>
 *
 * <p><b>Request</b></p>
 *
 * <p>The {@code request} substitution's expression should be one of two fixed values; {@code origin} or {@code path}.
 * If origin is specified the template will be substituted for the origin of the request. For example if the request
 * was {@code https://localhost:8080/rexy/api/endpoint} the value would be {@code https://localhost:8080/}. If
 * path is specified the template will be substituted for the path of the request. For example if the request
 * was {@code https://localhost:8080/rexy/api/endpoint} the value would be {@code /rexy/api/endpoint}. An example
 * template is:</p>
 *
 * <pre><code>{@code
 * ${request:origin}
 * }</code></pre>
 */
public class TemplateModule extends ModuleAdapter {
	
	private static final Pattern PATTERN = Pattern.compile("\\$\\{(.+?)}");
	
	public static final String PARAM = "param";
	public static final String HEADER = "header";
	public static final String XPATH = "xpath";
	public static final String JSONPATH = "jsonpath";
	public static final String REGEX = "regex";
	public static final String REQUEST = "request";
	
	private final TemplateProcessorFactory processorFactory = new TemplateProcessorFactory();
	
	@Override
	public void init(RexyDetails rexyDetails, JsonNode config) {
		processorFactory.addProcessor(new ParameterTemplateProcessor(PARAM));
		processorFactory.addProcessor(new HeaderTemplateProcessor(HEADER));
		processorFactory.addProcessor(new XpathTemplateProcessor(XPATH));
		processorFactory.addProcessor(new JsonPathTemplateProcessor(JSONPATH));
		processorFactory.addProcessor(new RegexTemplateProcessor(REGEX));
		processorFactory.addProcessor(new RequestTemplateProcessor(REQUEST));
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