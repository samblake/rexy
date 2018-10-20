package rexy.config.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * <p>Models the base config:</p>
 *
 * <p>{@code
 * {
 *   "port": "8081",
 *   "baseUrl": "/",
 *   "scanPackages": ["rexy.module"]
 *   "modules": {
 *     "mock": {
 *       ...
 *     },
 *     "proxy": {
 *       ...
 *     }
 *   },
 *   "apis": [
 *     ...
 *   ]
 * }
 * }</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"port",
		"baseUrl",
		"scanPackages",
		"modules",
		"apis"
})
public class Config {
	
	@JsonProperty("port")
	private int port;
	@JsonProperty("baseUrl")
	private String baseUrl;
	@JsonProperty("scanPackages")
	private List<String> scanPackages = emptyList();
	@JsonProperty("modules")
	private LinkedHashMap<String, JsonNode> modules = new LinkedHashMap<>();
	@JsonProperty("apis")
	private List<Api> apis = emptyList();
	
	public int getPort() {
		return port;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}
	
	public List<String> getScanPackages() {
		return scanPackages;
	}
	
	public LinkedHashMap<String, JsonNode> getModules() {
		return modules;
	}
	
	public List<Api> getApis() {
		return apis;
	}
	
}
