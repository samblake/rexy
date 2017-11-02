package rexy.config.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"port",
		"baseUrl",
		"features",
		"scanPackages",
		"apis"
})
public class Config {
	
	@JsonProperty("port")
	private int port;
	@JsonProperty("baseUrl")
	private String baseUrl;
	@JsonProperty("scanPackages")
	private List<String> scanPackages = new ArrayList<>();
	@JsonProperty("features")
	private LinkedHashMap<String, Feature> features = new LinkedHashMap<>();
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
	
	public List<String> getScanPackages() {
		return scanPackages;
	}
	
	public void setScanPackages(List<String> scanPackages) {
		this.scanPackages = scanPackages;
	}
	
	public LinkedHashMap<String, Feature> getFeatures() {
		return features;
	}
	
	public void setFeatures(LinkedHashMap<String, Feature> features) {
		this.features = features;
	}
	
	public List<Api> getApis() {
		return apis;
	}
	
	public void setApis(List<Api> apis) {
		this.apis = apis;
	}
}
