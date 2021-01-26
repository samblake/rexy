package com.github.samblake.rexy.utils.loader;

import java.io.IOException;

public interface Loader {
	
	String load(String path) throws IOException;
	
}