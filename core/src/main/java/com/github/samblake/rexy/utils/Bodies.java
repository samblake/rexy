package com.github.samblake.rexy.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.samblake.rexy.config.ConfigException;
import com.github.samblake.rexy.config.model.Response;
import com.github.samblake.rexy.utils.loader.ChainLoader;
import com.github.samblake.rexy.utils.loader.FileLoader;
import com.github.samblake.rexy.utils.loader.IdentityLoader;
import com.github.samblake.rexy.utils.loader.ResourceLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Bodies {
	private static final Logger logger = LogManager.getLogger(Bodies.class);
	
	private static final ChainLoader LOADER = new ChainLoader(
			new ResourceLoader(), new FileLoader(), new IdentityLoader());
	
	public static String findBody(Response response) {
		return response.getBody().map(Bodies::findBody).orElse(null);
	}
	
	private static String findBody(JsonNode node) {
		try {
			return node.isTextual() ? parse(node.asText()) : node.toPrettyString();
		}
		catch (ConfigException e) {
			throw new RuntimeException("Could not load config", e);
		}
	}
	
	private static String parse(String path) throws ConfigException {
		logger.info("Reading body from " + path);
		try {
			return LOADER.load(path);
		}
		catch (IOException e) {
			throw new ConfigException("Could not read " + path, e);
		}
	}
	
}