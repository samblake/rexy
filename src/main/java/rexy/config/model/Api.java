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
 * <p>{@code
 * {
 *   "name": "metaweather",
 *   "baseUrl": "api",
 *   "contentType": "application/json",
 *   "proxy": "http://www.metaweather.com/api",
 *   "endpoints": [
 *     ...
 *   ]
 * }
 * }</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"name",
		"baseUrl",
		"contentType",
		"proxy",
		"endpoints",
		"headers"
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
	@JsonManagedReference
	@JsonProperty("endpoints")
	private List<Endpoint> endpoints = emptyList();
	@JsonProperty("headers")
	private Map<String, String> headers = emptyMap();
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}
	
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public String getProxy() {
		return proxy;
	}
	
	public void setProxy(String proxy) {
		this.proxy = proxy;
	}
	
	public List<Endpoint> getEndpoints() {
		return endpoints;
	}
	
	public void setEndpoints(List<Endpoint> endpoints) {
		this.endpoints = endpoints;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
}