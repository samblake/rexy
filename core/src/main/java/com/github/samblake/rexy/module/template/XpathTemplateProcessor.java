package com.github.samblake.rexy.module.template;

import com.github.samblake.rexy.http.request.RexyRequest;
import com.github.samblake.rexy.utils.Xml;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathNodes;
import java.io.IOException;
import java.io.StringReader;

import static org.w3c.dom.Node.ATTRIBUTE_NODE;
import static org.w3c.dom.Node.TEXT_NODE;

public class XpathTemplateProcessor extends ExpressionTemplateProcessor {
	private static final Logger logger = LogManager.getLogger(XpathTemplateProcessor.class);
	
	public XpathTemplateProcessor(String name) {
		super(name);
	}
	
	@Override
	protected String substituteExpression(RexyRequest request, String xpath) throws SubstituteException {
		try {
			XPathNodes nodes = evaluateXpath(request, xpath);
			
			if (nodes.size() == 0) {
				logger.warn("No matches for XPath: " + xpath);
				return "";
			}
			
			if (nodes.size() > 1) {
				logger.warn(nodes.size() + " found, returning first node");
			}
			
			Node node = nodes.get(0);
			if (node.getNodeType() == TEXT_NODE || node.getNodeType() == ATTRIBUTE_NODE) {
				return node.getNodeValue();
			}
			
			return Xml.toString(new DOMSource(node), false);
		}
		catch (Exception e) {
			throw new SubstituteException("XPath could not be evaluated", e);
		}
	}
	
	private XPathNodes evaluateXpath(RexyRequest request, String xpath)
			throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		
		XPathExpression expr = XPathFactory.newInstance().newXPath().compile(xpath);
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource inputSource = new InputSource(new StringReader(request.getBody()));
		Document document = builder.parse(inputSource);
		return expr.evaluateExpression(document, XPathNodes.class);
	}
	
}