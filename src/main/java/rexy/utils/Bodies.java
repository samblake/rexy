package rexy.utils;

import com.fasterxml.jackson.databind.JsonNode;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.log4j.Logger;
import rexy.config.ConfigException;
import rexy.config.model.Response;
import rexy.module.jmx.mock.MockEndpoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Bodies {
	private static final Logger logger = Logger.getLogger(Bodies.class);
	
	public static String findBody(Response response) {
		return response.getBody().map(Bodies::findBody).orElse(null);
	}
	
	private static String findBody(JsonNode node) {
		try {
			return node.isTextual() ? parse(node.asText()) : node.toPrettyString();
		}
		catch (ConfigException e) {
			// FIXME improve error handling, the whole method probably need moving out of here
			throw new RuntimeException("Could not load config", e);
		}
	}
	
	@SuppressFBWarnings(
			value = {
					"NP_LOAD_OF_KNOWN_NULL_VALUE",
					"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE",
					"RCN_REDUNDANT_NULLCHECK_OF_NULL_VALUE"
			},
			justification = "Seems to be a false positive - https://github.com/spotbugs/spotbugs/issues/1338"
	)
	private static String parse(String path) throws ConfigException {
		logger.info("Attempting to read body from " + path);
		try {
			try (InputStream inputStream = MockEndpoint.class.getResourceAsStream('/' + path)) {
				if (inputStream != null) {
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8))) {
						return reader.lines().collect(Collectors.joining("\n"));
					}
				}
			}
			
			logger.debug(path + " not found on classpath, looking for absolute file");
			File file = new File(path);
			if (file.exists()) {
				try (BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(file), UTF_8))) {
					return reader.lines().collect(Collectors.joining("\n"));
				}
			}
			
			logger.debug(path + " absolute file not found, using string value");
			return path;
		}
		catch (IOException e) {
			throw new ConfigException("Could not read " + path, e);
		}
	}
	
}