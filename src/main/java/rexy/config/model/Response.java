package rexy.config.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"name",
		"httpStatus",
		"body"
})
public class Response {
	
	@JsonProperty("name")
	private String name;
	@JsonProperty("httpStatus")
	private int httpStatus;
	@JsonProperty("body")
	private JsonNode body;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int getHttpStatus() {
		return httpStatus;
	}
	
	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}
	
	public JsonNode getBody() {
		return body;
	}
	
	public void setBody(JsonNode body) {
		this.body = body;
	}
}