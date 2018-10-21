package rexy.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.module.jmx.mock.MockModule;

import java.io.IOException;

public class Json {
	private static final Logger logger = LogManager.getLogger(MockModule.class);
	
	private Json() {
	}
	
	public static boolean booleanValue(JsonNode node, String name) {
		JsonNode jsonValue = node.get(name);
		return jsonValue != null && jsonValue.isBoolean() && jsonValue.booleanValue();
	}
	
	public static Integer intValue(JsonNode node, String name, int def) {
		JsonNode jsonValue = node.get(name);
		return jsonValue != null && jsonValue.isInt() ? jsonValue.intValue() : def;
	}
	
	public static String stringValue(JsonNode node, String name, String def) {
		JsonNode jsonValue = node.get(name);
		return jsonValue != null && jsonValue.isTextual() ? jsonValue.textValue() : def;
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