package rexy.config.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * <p>Models the endpoint filter config:</p>
 *
 * <pre>{@code
 * {
 *   "type": "test",
 *   "query": "abc=123"
 * }
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"type",
		"query"
})
public class Filter {
	
	@JsonBackReference
	private Filter endpoint;
	@JsonProperty("type")
	private String type;
	@JsonProperty("query")
	private String method;
	
	public Filter getEndpoint() {
		return endpoint;
	}
	
	public void setEndpoint(Filter endpoint) {
		this.endpoint = endpoint;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getMethod() {
		return method;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	
}