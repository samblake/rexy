package com.github.samblake.rexy.module.wexy.actions.index;

import com.github.samblake.rexy.config.model.Api;
import com.github.samblake.rexy.http.RexyHandler;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class ApiLink {
	private final Api api;
	private final String link;
	
	public ApiLink(Api api, String link) {
		this.api = api;
		this.link = link;
	}
	
	public Api getApi() {
		return api;
	}
	
	public String getLink() {
		return link;
	}
	
	public static List<ApiLink> fromRoutes(Map<String, RexyHandler> routes) {
		return routes.entrySet().stream()
				.map(e -> new ApiLink(e.getValue().getApi(), e.getKey()))
				.collect(toList());
		
	}

}