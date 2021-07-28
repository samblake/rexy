package com.github.samblake.rexy.module.template;

import com.github.samblake.rexy.http.request.RexyRequest;

public interface TemplateProcessor {
	
	String getName();
	
	boolean matches(String template);
	
	String substitute(RexyRequest request, String template);
	
}