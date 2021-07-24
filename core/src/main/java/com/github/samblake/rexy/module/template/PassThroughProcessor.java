package com.github.samblake.rexy.module.template;

import com.github.samblake.rexy.http.request.RexyRequest;

public class PassThroughProcessor implements TemplateProcessor {
	
	@Override
	public String getName() {
		return "PassThrough";
	}
	
	@Override
	public boolean matches(String template) {
		return false;
	}
	
	@Override
	public String substitute(RexyRequest request, String template) {
		return "\\${" + template + "}";
	}
	
}