package rexy.doclet.example;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * I am the child!
 */
public class Child extends Parent {

    @JsonProperty("field")
    private String field;

}
