package com.github.samblake.rexy.config.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.samblake.rexy.config.RexyConfig;

import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * <p>The root configures the general details of the Rexy application.</p>
 *
 * <pre><code>{@code
 * {
 *   "port": "8081",
 *   "baseUrl": "/",
 *   "scanPackages": ["com.github.samblake.rexy.module"]
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
public class Config implements RexyConfig {

	/**
	 * The port that listens for incoming connections. Your server should point it's API clients
	 * to this port in order to proxy/mock them.
	 */
	@JsonProperty("port")
	private int port;

	/**
	 * The base path that all API endpoints run under. Defaults to <code>/</code>.
	 */
	@JsonProperty("baseUrl")
	private String baseUrl;

	/**
	 * The packages that should be scanned for modules.
	 */
	@JsonProperty("scanPackages")
	private List<String> scanPackages = emptyList();

	/**
	 * A map of module name to module specific configurations. All module configurations have, as a minimum,
	 * an {@code enabled} element. All other values are defined by the module itself. Modules are disabled
	 * by default and must be explicitly set to be enabled.
	 */
	@JsonProperty("modules")
	private LinkedHashMap<String, JsonNode> modules = new LinkedHashMap<>();

	/**
	 * Configuration details of the APIs that Rexy will handle.
	 */
	@JsonProperty("apis")
	private List<Api> apis = emptyList();

	/**
	 * A list of supplementary files, each containing a single API configuration.
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