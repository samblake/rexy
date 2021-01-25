package rexy.config.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;

/**
 * <p>Models a mock response preset.</p>
 *
 * <pre><code>{@code
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
 * }</code></pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"name",
		"httpStatus",
		"headers",
		"body"
})
public class Response {

	/**
	 * The name of the preset.
	 */
	@JsonProperty("name")
	private String name;

	/**
	 * The HTTP status code to return.
	 */
	@JsonProperty("httpStatus")
	private int httpStatus;

	/**
	 * The HTTP response headers to return.
	 */
	@JsonProperty("headers")
	private Map<String, String> headers = emptyMap();

	/**
	 * The response body to return. If the response is JSON the the object can be supplied directly in the config,
	 * otherwise a reference to a text file containing the body should be supplied.
	 */
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
	
	public Optional<JsonNode> getBody() {
		return ofNullable(body);
	}
	
}