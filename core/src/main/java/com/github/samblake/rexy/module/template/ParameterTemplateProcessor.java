package com.github.samblake.rexy.module.template;

import com.github.samblake.rexy.http.request.RexyRequest;

import java.util.Map.Entry;

public class ParameterTemplateProcessor extends ExpressionTemplateProcessor {
	
	public ParameterTemplateProcessor(String name) {
		super(name);
	}
	
	@Override
	protected String substituteExpression(RexyRequest request, String parameterName) {
		return request.getParameters().entrySet().stream()
				.filter(p -> p.getKey().equals(parameterName))
				.findFirst()
				.map(Entry::getValue)
				.orElse("");
	}
	
}