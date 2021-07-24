package com.github.samblake.rexy.module.jmx.matcher;

import com.fasterxml.jackson.databind.JsonNode;
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

/**
 * <p>Matches requests with bodies that match a given XPath expression. As such it should be used with requests
 * that contain an XML body. The expression should be supplied as the configuration. For example:
 *
 * <pre><code>{@code
 * "matchers": {
 *   "xpath": "//elements/element"
 * },
 * }</code></pre>
 *
 * @param <T> The type of MBean
 */
public final class XpathBodyMatcher<T> extends RequestBodyMatcher<T> {
	private static final Logger logger = LogManager.getLogger(JmxRegistry.class);
	
	private final XPathExpression expression;
	private final String xPath;
	
	private XpathBodyMatcher(String xPath, XPathExpression expression, T mBean) {
		super(mBean);
		this.xPath = xPath;
		this.expression = expression;
	}
	
	static <T> RequestMatcher<T> create(JsonNode config, T mBean) {
		String xpath = Json.textValue(config);
		if (xpath == null) {
			// TODO non-runtime exception
			throw new RuntimeException("XPath expression not set");
		}
		
		try {
			XPathExpression expr = XPathFactory.newInstance().newXPath().compile(xpath);
			return new XpathBodyMatcher<>(xpath, expr, mBean);
		}
		catch (XPathExpressionException e) {
			// TODO non-runtime exception
			throw new RuntimeException("Invalid XPath expression");
		}
	}
	
	@Override
	protected boolean matches(String body) {
		if (logger.isDebugEnabled()) {
			logger.debug("Testing XPath " + expression);
		}
		
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource inputSource = new InputSource(new StringReader(body));
			Document document = builder.parse(inputSource);
			
			XPathNodes nodes = expression.evaluateExpression(document, XPathNodes.class);
			return nodes.size() > 0;
		}
		catch (XPathExpressionException e) {
			logger.error("Invalid XPath expression: " + expression);
			return false;
		}
		catch (ParserConfigurationException | IOException | SAXException e) {
			logger.error("Failed to parse request body: " + body);
			return false;
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + ':' + xPath;
	}
	
}