package com.github.samblake.rexy.module.wexy.model;

import com.github.jknack.handlebars.Handlebars;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Template {
	
	private final com.github.jknack.handlebars.Template template;
	private final Map<String, Object> model;
	
	public Template(Handlebars handlebars, String templateName) throws IOException {
		this.template = handlebars.compile(templateName);
		this.model = new HashMap<>();
	}
	
	public Template with(String key, Object value) {
		model.put(key, value);
		return this;
	}
	
	public Template withBreadcrumbs(Breadcrumbs crumbs) {
		return with("crumbs", crumbs);
	}
	
	public Template withNotification(Notification notification) {
		return with("notification", notification);
	}
	
	public String apply() throws IOException {
		return template.apply(model);
	}
	
}