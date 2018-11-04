package rexy.config.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import rexy.http.Method;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * <p>Models the endpoint config:</p>
 *
 * <p>{@code
 * {
 *   "name": "location",
 *   "method": "GET",
 *   "endpoint": "/location/search/?query={query}",
 *   "responses": [
 *     ...
 *   ]
 * }
 * }</p>
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
	@JsonProperty("name")
	private String name;
	@JsonProperty("method")
	private Method method;
	@JsonProperty("endpoint")
	private String endpoint;
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
		return method;
	}
	
	public String getEndpoint() {
		return endpoint;
	}
	
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	public List<Response> getResponses() {
		return responses;
	}
	
	public void setResponses(List<Response> responses) {
		this.responses = responses;
	}
	
}