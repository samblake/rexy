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
 *   ],
 *   "imports": [
 *     ...
 *   ]
 * }
 * }</p>
 *
 * <p>In addition to the {@link rexy.config.RexyConfig standard config} it also includes paths to JSON files
 * to allow importing of externally defined APIs.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"port",
		"baseUrl",
		"scanPackages",
		"modules",
		"apis",
		"imports"
})
public class Config implements rexy.config.RexyConfig {
	
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
	@JsonProperty("imports")
	private List<String> imports = emptyList();
	
	@Override
	public int getPort() {
		return port;
	}
	
	@Override
	public String getBaseUrl() {
		return baseUrl;
	}
	
	@Override
	public List<String> getScanPackages() {
		return scanPackages;
	}
	
	@Override
	public LinkedHashMap<String, JsonNode> getModules() {
		return modules;
	}
	
	@Override
	public List<Api> getApis() {
		return apis;
	}
	
	public List<String> getImports() {
		return imports;
	}
	
}
