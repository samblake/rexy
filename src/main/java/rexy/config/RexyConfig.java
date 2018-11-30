package rexy.config;

import com.fasterxml.jackson.databind.JsonNode;
import rexy.config.model.Api;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * The configuration required for running Rexy proper.
 */
public interface RexyConfig {
	
	int getPort();
	
	String getBaseUrl();
	
	List<String> getScanPackages();
	
	LinkedHashMap<String, JsonNode> getModules();
	
	List<Api> getApis();
	
}
