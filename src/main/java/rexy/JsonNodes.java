package rexy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.module.jmx.mock.MockModule;

import java.io.IOException;

public class JsonNodes {
	private static final Logger logger = LogManager.getLogger(MockModule.class);
	
	private JsonNodes() {
	}
	
	public static boolean booleanValue(JsonNode config, String name) {
		JsonNode jsonNode = config.get(name);
		return jsonNode != null && jsonNode.isBoolean() && jsonNode.booleanValue();
	}
	
	public static String prettyPrint(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readValue(json, JsonNode.class);
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
		}
		catch (IOException e) {
			logger.warn("Could not pretty-print JSON: " + json);
			return json;
		}
	}
	
}