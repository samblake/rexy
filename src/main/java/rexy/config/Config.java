package rexy.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"port",
		"baseUrl",
		"features",
		"apis"
})
public class Config {
	
	@JsonProperty("port")
	private int port;
	@JsonProperty("baseUrl")
	private String baseUrl;
	@JsonProperty("features")
	private List<String> features = new ArrayList<>();
	@JsonProperty("apis")
	private List<Api> apis = new ArrayList<>();
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}
	
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public List<String> getFeatures() {
		return features;
	}
	
	public void setFeatures(List<String> features) {
		this.features = features;
	}
	
	public List<Api> getApis() {
		return apis;
	}
	
	public void setApis(List<Api> apis) {
		this.apis = apis;
	}
}
