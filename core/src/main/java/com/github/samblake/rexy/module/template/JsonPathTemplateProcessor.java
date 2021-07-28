package com.github.samblake.rexy.module.template;

import com.github.samblake.rexy.http.request.RexyRequest;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonPathTemplateProcessor extends ExpressionTemplateProcessor {
	private static final Logger logger = LogManager.getLogger(JsonPathTemplateProcessor.class);
	
	public JsonPathTemplateProcessor(String name) {
		super(name);
	}
	
	@Override
	protected String substituteExpression(RexyRequest request, String jsonPath) throws SubstituteException {
		try {
			return JsonPath.parse(request.getBody()).read(jsonPath).toString();
		}
		catch (PathNotFoundException e) {
			return "";
		}
		catch (Exception e) {
			throw new SubstituteException("XPath could not be evaluated", e);
		}
	}
	
}