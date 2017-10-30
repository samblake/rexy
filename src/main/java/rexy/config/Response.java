
package rexy.config;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRawValue;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.JsonNode;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "httpStatus",
    "body"
})
public class Response {

    @JsonProperty("httpStatus")
    private int httpStatus;
    @JsonProperty("body")
    private JsonNode body;

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