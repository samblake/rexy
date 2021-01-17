package rexy.utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.log4j.Logger;
import rexy.config.ConfigException;
import rexy.config.model.Response;
import rexy.utils.loader.ChainLoader;
import rexy.utils.loader.FileLoader;
import rexy.utils.loader.IdentityLoader;
import rexy.utils.loader.ResourceLoader;

import java.io.IOException;

public class Bodies {
	private static final Logger logger = Logger.getLogger(Bodies.class);
	
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