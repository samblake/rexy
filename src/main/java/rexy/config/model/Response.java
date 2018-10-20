package rexy.config.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

import static java.util.Collections.emptyMap;

/**
 * <p>Models the response config:</p>
 *
 * <p>{@code
 * {
 *   "name": "Successful",
 *   "httpStatus": 200,
 *   "headers": {
 *     "abc": "Test 1",
 *     "xyz": "Test 2"
 *   }
 *   "body": {
 *     "title": "London",
 *     "location_type": "City",
 *     "woeid": 44418,
 *     "latt_long": "51.506321,-0.12714"
 *   }
 * }
 * }</p>
 */
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
	@JsonProperty("headers")
	private Map<String, String> headers = emptyMap();
	@JsonProperty("body")
	private JsonNode body;
	
	public String getName() {
		return name;
	}
	
	public int getHttpStatus() {
		return httpStatus;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public JsonNode getBody() {
		return body;
	}
	
}