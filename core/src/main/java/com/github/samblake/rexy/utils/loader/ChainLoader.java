package com.github.samblake.rexy.utils.loader;

import org.apache.log4j.Logger;

import java.io.IOException;

public class ChainLoader implements Loader {
	private static final Logger logger = Logger.getLogger(ChainLoader.class);
	
	private final Loader[] loaders;
	
	public ChainLoader(Loader... loaders) {
		this.loaders = loaders;
	}
	
	@Override
	public String load(String path) throws IOException {
		for (Loader loader : loaders) {
			if (logger.isDebugEnabled()) {
				logger.debug("Attempting to load " + path + " with " + loader.getClass().getSimpleName());
			}
			
			String result = loader.load(path);
			if (result != null) {
				if (logger.isDebugEnabled()) {
					logger.debug(loader.getClass().getSimpleName() + " could not load " + path);
				}
				return result;
			}
		}
		
		logger.warn(path + " could not be found");
		return null;
	}
	
}