package com.github.samblake.rexy.module.template;

import com.github.samblake.rexy.http.request.RexyRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;

import static java.lang.Math.min;
import static java.util.regex.Pattern.compile;

public class RegexTemplateProcessor extends ExpressionTemplateProcessor {
	private static final Logger logger = LogManager.getLogger(RegexTemplateProcessor.class);
	
	public RegexTemplateProcessor(String name) {
		super(name);
	}
	
	@Override
	protected String substituteExpression(RexyRequest request, String regex) {
		Matcher matcher = compile(regex).matcher(request.getBody());
		if (!matcher.find()) {
			logger.warn("No matches for regular expression: " + regex);
			return "";
		}
		
		return matcher.group(min(matcher.groupCount(), 1));
	}
	
}