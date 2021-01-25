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
 * <p>Models an API that Rexy can handle. Each will run under it's own path.</p>
 *
 * <pre><code>{@code
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
 * }</code></pre>
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

	/**
	 * The name of the API.
	 */
	@JsonProperty("name")
	private String name;

	/**
	 * The path the API will be deployed to.
	 */
	@JsonProperty("baseUrl")
	private String baseUrl;

	/**
	 * The default content type to use for mocked API responses. This can be overridden by the endpoint or response.
	 */
	@JsonProperty("contentType")
	private String contentType;

	/**
	 * The URL that requests to this API should be proxied to.
	 */
	@JsonProperty("proxy")
	private String proxy;

	/**
	 * The default headers to use for mocked API responses. This can be overridden by the endpoint or response.
	 */
	@JsonProperty("headers")
	private Map<String, String> headers = emptyMap();

	/**
	 * The endpoints that are provided by the API.
	 */
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