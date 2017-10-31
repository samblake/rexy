package rexy.config.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"enabled"
})
public class Feature {
	
	@JsonProperty("enabled")
	private boolean enabled;
	@JsonIgnore
	private Map<String, Object> config = new HashMap<>();
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@JsonAnyGetter
	public Map<String, Object> getConfig() {
		return this.config;
	}
	
	@JsonAnySetter
	public void setConfig(String name, Object value) {
		this.config.put(name, value);
	}
}