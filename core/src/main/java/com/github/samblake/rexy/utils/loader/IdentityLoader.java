package com.github.samblake.rexy.utils.loader;

public class IdentityLoader implements Loader {
	
	@Override
	public String load(String path) {
		return path;
	}
	
}