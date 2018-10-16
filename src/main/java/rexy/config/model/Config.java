package rexy.config.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.List;

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
	
	private final int port;
	private final String baseUrl;
	private final List<String> scanPackages;
	private final LinkedHashMap<String, JsonNode> modules;
	private final List<Api> apis;
	
	public Config(@JsonProperty("port") int port,
	              @JsonProperty("baseUrl") String baseUrl,
	              @JsonProperty("scanPackages") List<String> scanPackages,
	              @JsonProperty("modules") LinkedHashMap<String, JsonNode> modules,
	              @JsonProperty("apis") List<Api> apis) {
		this.port = port;
		this.baseUrl = baseUrl;
		this.scanPackages = scanPackages;
		this.modules = modules;
		this.apis = apis;
	}
	
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
