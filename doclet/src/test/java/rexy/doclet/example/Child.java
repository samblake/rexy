package rexy.doclet.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * I am the child!
 */
@JsonPropertyOrder({
        "field"
})
public class Child extends Parent {

    @JsonProperty("field")
    private String field;

}
