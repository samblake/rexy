package com.github.samblake.rexy.module.template;

import com.github.samblake.rexy.http.request.RexyRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ExpressionTemplateProcessor implements TemplateProcessor {
	private static final Logger logger = LogManager.getLogger(ExpressionTemplateProcessor.class);
	
	private final String name;
	
	public ExpressionTemplateProcessor(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public boolean matches(String template) {
		return template.startsWith(name + ":");
	}
	
	@Override
	public String substitute(RexyRequest request, String template) {
		String expression = template.substring(template.indexOf(":") + 1);
		String substitution = substituteExpression(request, template, expression);
		
		logger.debug("Substituting ${" + name + ":" + template + "} with " + substitution);
		return substitution;
	}
	
	private String substituteExpression(RexyRequest request, String template, String expression) {
		try {
			return substituteExpression(request, expression);
		}
		catch (SubstituteException e) {
			logger.warn("Could not substitution for " + template, e);
			return "\\${" + template + "}";
		}
	}
	
	protected abstract String substituteExpression(RexyRequest request, String expression) throws SubstituteException;
	
	protected static class SubstituteException extends Exception {
		
		public SubstituteException(String message, Exception e) {
			super(message, e);
		}
	}
	
}