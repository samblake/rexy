package rexy.config.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

/**
 * <p>Models the API config:</p>
 *
 * <pre>{@code
 * {
 *   "name": "metaweather",
 *   "baseUrl": "api",
 *   "contentType": "application/json",
 *   "proxy": "http://www.metaweather.com/api",
 *   "headers": {
 *     "Accept-Charset": "utf-8"
 *   },
 *   "endpoints": [
 *     ...
 *   ]
 * }
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"name",
		"baseUrl",
		"contentType",
		"proxy",
		"headers",
		"endpoints"
})
public class Api {
	
	@JsonProperty("name")
	private String name;
	@JsonProperty("baseUrl")
	private String baseUrl;
	@JsonProperty("contentType")
	private String contentType;
	@JsonProperty("proxy")
	private String proxy;
	@JsonProperty("headers")
	private Map<String, String> headers = emptyMap();
	@JsonManagedReference
	@JsonProperty("endpoints")
	private List<Endpoint> endpoints = emptyList();
	
	public String getName() {
		return name;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public String getProxy() {
		return proxy;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public List<Endpoint> getEndpoints() {
		return endpoints;
	}
	
}