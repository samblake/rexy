package com.github.samblake.rexy.module.template;

import com.github.samblake.rexy.http.RexyHeader;
import com.github.samblake.rexy.http.request.RexyRequest;

public class HeaderTemplateProcessor extends ExpressionTemplateProcessor {
	
	public HeaderTemplateProcessor(String name) {
		super(name);
	}
	
	@Override
	protected String substituteExpression(RexyRequest request, String headerName) {
		return request.getHeaders().stream()
				.filter(p -> p.getName().equals(headerName))
				.findFirst()
				.map(RexyHeader::getValue)
				.orElse("");
	}
	
}