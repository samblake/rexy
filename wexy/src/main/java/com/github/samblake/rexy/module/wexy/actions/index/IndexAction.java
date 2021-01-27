package com.github.samblake.rexy.module.wexy.actions.index;

import com.github.jknack.handlebars.Handlebars;
import com.github.samblake.rexy.config.model.Api;
import com.github.samblake.rexy.http.RexyHandler;
import com.github.samblake.rexy.http.response.RexyResponse;
import com.github.samblake.rexy.module.wexy.actions.WexyAction;
import com.github.samblake.rexy.module.wexy.model.Template;

import java.io.IOException;
import java.util.Map;

public class IndexAction extends WexyAction {
	
	private final Map<String, RexyHandler> routes;
	
	public IndexAction(String baseUrl, Handlebars handlebars, Map<String, RexyHandler> routes) {
		super(baseUrl, handlebars);
		this.routes = routes;
	}
	
	@Override
	public RexyResponse perform(Api api, Map<String, String> params) {
		return perform();
	}
	
	public RexyResponse perform() {
		try {
			Template template = createTemplate("index", createBreadcrumbs())
					.with("apis", ApiLink.fromRoutes(routes));
			
			return createResponse(template);
		}
		catch (IOException e) {
			throw new RuntimeException("Could perform request", e);
		}
	}
	
}