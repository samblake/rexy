package com.github.samblake.rexy.module.wexy.actions;

import com.github.jknack.handlebars.Handlebars;
import com.github.samblake.rexy.config.model.Api;
import com.github.samblake.rexy.http.response.BasicRexyResponse;
import com.github.samblake.rexy.http.response.RexyResponse;
import com.github.samblake.rexy.module.wexy.builders.BreadcrumbBuilder.HomeCrumbBuilder;
import com.github.samblake.rexy.module.wexy.builders.Builder;
import com.github.samblake.rexy.module.wexy.model.Breadcrumbs;
import com.github.samblake.rexy.module.wexy.model.Template;

import java.io.IOException;
import java.util.Map;

import static fi.iki.elonen.NanoHTTPD.MIME_HTML;
import static java.nio.charset.Charset.defaultCharset;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;

public abstract class WexyAction {
	
	private static final int REDIRECT = 301;
	private static final String HTML = "text/html";
	private static final byte[] BODY = new byte[0];
	
	protected final String baseUrl;
	private final Handlebars handlebars;
	
	public WexyAction(String baseUrl, Handlebars handlebars) {
		this.baseUrl = baseUrl;
		this.handlebars = handlebars;
	}
	
	public abstract RexyResponse perform(Api api, Map<String, String> params);
	
	protected RexyResponse createResponse(Template template) throws IOException {
		byte[] body = template.apply().getBytes(defaultCharset());
		return new BasicRexyResponse(200, emptyMap(), MIME_HTML, body);
	}
	
	protected Template createTemplate(String name, Builder<Breadcrumbs> breadcrumbs) throws IOException {
		return new Template(handlebars, name).withBreadcrumbs(breadcrumbs.build());
	}
	
	protected HomeCrumbBuilder createBreadcrumbs() {
		return new HomeCrumbBuilder(baseUrl);
	}
	
	protected RexyResponse createRedirect(String location) {
		return new BasicRexyResponse(REDIRECT, singletonMap("Location", location), HTML, BODY);
	}
	
}