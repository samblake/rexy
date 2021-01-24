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
 * <pre><code>{@code
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
 * }</code></pre>
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

	/**
	 * The port that listens for incoming connections.
	 */
	@JsonProperty("port")
	private int port;

	/**
	 * The base path that all API endpoints run under.
	 */
	@JsonProperty("baseUrl")
	private String baseUrl;

	/**
	 * Additional packages that will be scanned for modules.
	 */
	@JsonProperty("scanPackages")
	private List<String> scanPackages = emptyList();

	/**
	 * The modules that will be enabled.
	 */
	@JsonProperty("modules")
	private LinkedHashMap<String, JsonNode> modules = new LinkedHashMap<>();

	/**
	 * The APIs that will be running.
	 */
	@JsonProperty("apis")
	private List<Api> apis = emptyList();

	/**
	 * Configuration files to be imported as APIs. Relative to the base config file
	 */
	@JsonProperty("imports")
	private List<String> imports = emptyList();
	
	@Override
	public int getPort() {
		return port;
	}
	
	@Override
	public String getBasePath() {
		return baseUrl == null ? "/" : baseUrl;
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