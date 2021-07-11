package com.github.samblake.rexy.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.function.Supplier;

public class Json {
	private static final Logger logger = LogManager.getLogger(Json.class);
	
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
	
	public static String textValue(JsonNode node) {
		return textValue(node, (String)null);
	}
	
	public static String textValue(JsonNode node, String def) {
		return node instanceof TextNode && node.textValue() != null ? node.textValue() : def;
	}
	
	public static String stringValue(JsonNode node, String name) {
		return stringValue(node, name, (String)null);
	}
	
	public static String stringValue(JsonNode node, String name, String def) {
		JsonNode jsonValue = node.get(name);
		return jsonValue != null && jsonValue.isTextual() ? jsonValue.textValue() : def;
	}
	
	public static String stringValue(JsonNode node, String name, Supplier<String> def) {
		JsonNode jsonValue = node.get(name);
		return jsonValue != null && jsonValue.isTextual() ? jsonValue.textValue() : def.get();
	}
	
	public static String prettyPrint(String json) {
		if (json == null) {
			return json;
		}
		
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