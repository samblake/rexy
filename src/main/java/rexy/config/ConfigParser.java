package rexy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.model.Api;
import rexy.config.model.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses the JSON configuration. The path can either be relative to the classpath or an absolute path on
 * the filesystem. The classpath is checked before the filesystem.
 */
public class ConfigParser {
	private static final Logger logger = LogManager.getLogger(ConfigParser.class);

	private final String path;
	private final ObjectMapper mapper;
	
	/**
	 * Creates a configuration parser for the file at the supplied path.
	 *
	 * @param path The path to the configuration file relative to the classpath or absolute path
	 */
	public ConfigParser(String path) {
		this.path = path;
		this.mapper = new ObjectMapper();
	}
	
	/**
	 * Parses the JSON configuration. Imported APIs will be parsed and populated.
	 *
	 * @return The fully populated config
	 * @throws ConfigException Thrown if the configuration or any imported files cannot be found
	 */
	public RexyConfig parse() throws ConfigException {
		Config config = parse(Config.class, path);

		List<Api> importedApis = new ArrayList<>();
		for (String importPath : config.getImports()) {
			importedApis.add(parse(Api.class, importPath));
		}
		
		RexyConfig apiConfig = new ApiConfig(config, importedApis);
		// TODO validate (duplicate APIs, paths, etc.)
		return apiConfig;
	}
	
	private <T> T parse(Class<T> jacksonClass, String path) throws ConfigException {
		logger.info("Reading " + jacksonClass.getSimpleName() + " from " + path);
		try {
			try (InputStream inputStream = getClass().getResourceAsStream('/' + path)) {
				if (inputStream != null) {
					return mapper.readValue(inputStream, jacksonClass);
				}
			}
			
			logger.debug(jacksonClass.getSimpleName() + " not found on classpath, looking for absolute file");
			return mapper.readValue(new File(path), jacksonClass);
		}
		catch (IOException e) {
			throw new ConfigException("Could not read " + path, e);
		}
	}
	
}