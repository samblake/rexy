package com.github.samblake.rexy.module.jmx.matcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.samblake.rexy.http.request.RexyRequest;
import com.github.samblake.rexy.module.jmx.JmxRegistry;
import com.github.samblake.rexy.utils.Json;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.util.regex.Pattern.compile;

/**
 * <p>Matches requests with bodies that match a given regular expression. The expression should be supplied as the
 * configuration. For example:
 *
 * <pre><code>{@code
 * "matchers": {
 *   "regex": "^error"
 * },
 * }</code></pre>
 *
 * @param <T> The type of MBean
 */
public final class RegexBodyMatcher<T> extends RequestBodyMatcher<T> {
	private static final Logger logger = LogManager.getLogger(JmxRegistry.class);
	
	private final Pattern pattern;
	
	private RegexBodyMatcher(Pattern pattern, T mBean) {
		super(mBean);
		this.pattern = pattern;
	}
	
	static <T> RequestMatcher<T> create(JsonNode config, T mBean) {
		String expression = Json.textValue(config);
		if (expression == null) {
			// TODO non-runtime exception
			throw new RuntimeException("Regular expression not set");
		}
		
		try {
			Pattern pattern = compile(expression);
			return new RegexBodyMatcher<T>(pattern, mBean);
		}
		catch (PatternSyntaxException e) {
			// TODO non-runtime exception
			throw new RuntimeException("Invalid regular expression");
		}
	}
	
	@Override
	protected boolean matches(String body) {
		if (logger.isDebugEnabled()) {
			logger.debug("Testing regular expression " + pattern.pattern());
		}
		
		Matcher matcher = pattern.matcher(body);
		return matcher.matches();
	}
	
	@Override
	public String toString() {
		return super.toString() + ':' + pattern.pattern();
	}
	
}