package com.github.samblake.rexy.module.jmx.matcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.samblake.rexy.module.jmx.JmxRegistry;
import com.github.samblake.rexy.utils.Json;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathNodes;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static com.jayway.jsonpath.Option.ALWAYS_RETURN_LIST;

/**
 * <p>Matches requests with bodies that match a given
 * <a href="https://tools.ietf.org/id/draft-goessner-dispatch-jsonpath-00.html">JsonPath</a> expression. As such it
 * should be used with requests that contain a JSON body. The expression should be supplied as the configuration.
 * For example:
 *
 * <pre><code>{@code
 * "matchers": {
 *   "jsonpath": "$['object']['value']"
 * },
 * }</code></pre>
 *
 * @param <T> The type of MBean
 */
public final class JsonPathBodyMatcher<T> extends RequestBodyMatcher<T> {
	private static final Logger logger = LogManager.getLogger(JmxRegistry.class);
	
	private final JsonPath expression;
	private final Configuration configuration;
	
	private JsonPathBodyMatcher(JsonPath expression, T mBean) {
		super(mBean);
		this.expression = expression;
		
		this.configuration = Configuration.builder().options(ALWAYS_RETURN_LIST).build();
	}
	
	static <T> RequestMatcher<T> create(JsonNode config, T mBean) {
		String jsonPath = Json.textValue(config);
		if (jsonPath == null) {
			// TODO non-runtime exception
			throw new RuntimeException("JsonPath expression not set");
		}
		
		try {
			JsonPath expr = JsonPath.compile(jsonPath);
			return new JsonPathBodyMatcher<>(expr, mBean);
		}
		catch (InvalidPathException e) {
			// TODO non-runtime exception
			throw new RuntimeException("Invalid JsonPath expression");
		}
	}
	
	@Override
	protected boolean matches(String body) {
		if (logger.isDebugEnabled()) {
			logger.debug("Testing xPath " + expression);
		}
		
		try {
			List<?> result = JsonPath
					.using(configuration)
					.parse(body)
					.read(expression);
			
			return!result.isEmpty();
		}
		catch (InvalidJsonException e) {
			logger.error("Failed to parse request body: " + body);
			return false;
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + ':' + expression.getPath();
	}
	
}