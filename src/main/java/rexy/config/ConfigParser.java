package rexy.config;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rexy.config.model.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Parses the JSON configuration. The path can either be relative to the classpath or an absolute path on
 * the filesystem. The classpath is checked before the filesystem.
 */
public class ConfigParser {
	private static final Logger logger = LoggerFactory.getLogger(ConfigParser.class);
	
	private final String path;
	
	public ConfigParser(String path) {
		this.path = path;
	}
	
	public Config parse() throws ConfigException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			InputStream inputStream = getClass().getResourceAsStream('/' + path);
			if (inputStream != null) {
				return mapper.readValue(inputStream, Config.class);
			}
			
			logger.debug("Config not found on classpath, looking for absolute file");
			return mapper.readValue(new File(path), Config.class);
		}
		catch (IOException e) {
			throw new ConfigException("Could not read " + path, e);
		}
	}
}