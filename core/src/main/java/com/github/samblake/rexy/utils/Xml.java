package com.github.samblake.rexy.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

import static javax.xml.transform.OutputKeys.INDENT;

public class Xml {
	private static final Logger logger = LogManager.getLogger(Xml.class);
	
	private Xml() {
	}
	
	public static String prettyPrint(String xml) {
		if (xml == null) {
			return xml;
		}
		
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			
			StreamSource source = new StreamSource(new StringReader(xml));
			StreamResult result = new StreamResult(new StringWriter());
			transformer.transform(source, result);
			return result.getWriter().toString();
		}
		catch (TransformerException e) {
			logger.warn("Could not pretty-print XML: " + xml);
			return xml;
		}
	}
	
}