package rexy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.model.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Parses the JSON configuration. The path can either be relative to the classpath or an absolute path on
 * the filesystem. The classpath is checked before the filesystem.
 */
public class ConfigParser {
	private static final Logger logger = LogManager.getLogger(ConfigParser.class);

	private final String path;
	
	/**
	 * Creates a configuration parser for the file at the supplied path.
	 *
	 * @param path The path to the configuration file relative to the classpath or absolute path
	 */
	public ConfigParser(String path) {
		this.path = path;
	}
	
	/**
	 * Parses the JSON configuration.
	 *
	 * @return The model representation of the config
	 * @throws ConfigException Thrown if the configuration file cannot be found
	 */
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