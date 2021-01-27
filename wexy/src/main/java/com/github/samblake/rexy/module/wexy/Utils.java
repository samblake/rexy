package com.github.samblake.rexy.module.wexy;

import com.github.samblake.rexy.config.model.Endpoint;

public class Utils {

	private Utils() {
	}
	
	public static String until(String value, char c) {
		int idx = value.indexOf(c);
		return idx == -1 ? value : value.substring(0, idx);
	}
	
	public static String toUrl(Endpoint endpoint) {
		return endpoint.getName().replace(' ', '-');
	}
	
}
