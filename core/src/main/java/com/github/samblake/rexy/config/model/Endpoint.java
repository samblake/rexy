package com.github.samblake.rexy.config.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.samblake.rexy.http.Method;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

/**
 * <p>Models an endpoint for an API.</p>
 *
 * <pre><code>{@code
 * {
 *   "name": "location",
 *   "method": "GET",
 *   "endpoint": "location/search/?query={query}",
 *   "matchers": {
 *      ...
 *   },
 *   "responses": [
 *     ...
 *   ]
 * }
 * }</code></pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"name",
		"method",
		"endpoint",
		"responses"
})
public class Endpoint {
	
	@JsonBackReference
	private Api api;

	/**
	 * The name of the endpoint.
	 */
	@JsonProperty("name")
	private String name;

	/**
	 * The HTTP method of the endpoint.
	 */
	@JsonProperty("method")
	private Method method;

	/**
	 * The path of the endpoint. Named parameters can be specified in curly braces.
	 */
	@JsonProperty("endpoint")
	private String endpoint;
	
	/**
	 * Key-value pairs of matcher name to custom JSON configuration.
	 */
	@JsonProperty("matchers")
	private Map<String, JsonNode> matchers;

	/**
	 * Mock response presets.
	 */
	@JsonProperty("responses")
	private List<Response> responses = emptyList();
	
	public Api getApi() {
		return api;
	}
	
	public void setApi(Api api) {
		this.api = api;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Method getMethod() {
		return method == null ? Method.GET : method;
	}
	
	public String getEndpoint() {
		return endpoint;
	}
	
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	public Map<String, JsonNode> getMatchers() {
		return matchers;
	}
	
	public List<Response> getResponses() {
		return responses;
	}
	
	public void setResponses(List<Response> responses) {
		this.responses = responses;
	}
	
}