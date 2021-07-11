package com.github.samblake.rexy.utils;

import java.util.regex.Pattern;

public class Paths {
	
	private static final Pattern SLASH = Pattern.compile("//");
	
	private Paths() {
	}
	
	public static String join(String... partialPaths) {
		return SLASH.matcher(String.join("/", partialPaths)).replaceAll("/");
	}

}