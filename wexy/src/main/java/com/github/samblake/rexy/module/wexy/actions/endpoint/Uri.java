package com.github.samblake.rexy.module.wexy.actions.endpoint;

import com.github.samblake.rexy.http.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Uri {
	private static final Pattern PATTERN = Pattern.compile("\\{(.+?)}");
	
	private final Method method;
	private final String baseUrl;
	private final String template;
	private final List<String> parameters;
	
	private Uri(Method method, String baseUrl, String template, List<String> parameters) {
		this.method = method;
		this.baseUrl = baseUrl;
		this.template = template;
		this.parameters = parameters;
	}
	
	public String getMethod() {
		return method.name();
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}
	
	public String getTemplate() {
		return template;
	}
	
	public List<String> getParameters() {
		return parameters;
	}
	
	public boolean isParameterised() {
		return !parameters.isEmpty();
	}
	
	public String getPath() {
		return template.indexOf('?') == -1 ? template : template.substring(0, template.indexOf('?'));
	}
	
	public static Uri fromUrl(Method method, String baseUrl, String path) {
		Matcher matcher = PATTERN.matcher(path);
		List<String> parameters = new ArrayList<>();
		while (matcher.find()) {
			parameters.add(matcher.group(1));
		}
		return new Uri(method, baseUrl, path, parameters);
	}

}