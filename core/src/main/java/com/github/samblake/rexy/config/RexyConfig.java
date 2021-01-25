package com.github.samblake.rexy.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.samblake.rexy.module.RexyModule;
import com.github.samblake.rexy.config.model.Api;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * The configuration required for running Rexy.
 */
public interface RexyConfig {
	
	/**
	 * The port that Rexy should listen on.
	 */
	int getPort();
	
	/**
	 * The base path Rexy runs under. Defaults to <pre><code>{@code /}</code></pre>.
	 */
	String getBasePath();
	
	/**
	 * The packages that should be scanned for {@link RexyModule modules}.
	 */
	List<String> getScanPackages();
	
	/**
	 * A map of module name to module specific configurations. All module configurations have, as a minimum,
	 * an {@code enabled} element. All other values are defined by the module itself. Modules are disabled
	 * by default and must be explicitly set to be enabled.
	 */
	LinkedHashMap<String, JsonNode> getModules();
	
	/**
	 * Configuration details of the APIs that Rexy will handle.
	 */
	List<Api> getApis();
	
}
