package rexy.config.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

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
	@JsonProperty("endpoints")
	private List<Endpoint> endpoints = new ArrayList<>();
	@JsonProperty("headers")
	private Headers headers = new Headers();
	
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
	
	public Headers getHeaders() {
		return headers;
	}
	
	public void setHeaders(Headers headers) {
		this.headers = headers;
	}
}
