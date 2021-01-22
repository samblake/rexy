package rexy.doclet.example;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * <p>And <i>I</i> am the...</p>
 * <p><b>Parent!</b></p>
 */
public class Parent extends GrandParent implements Inter {

    private Child child;

    /**
     * Some sort of counting thing.
     */
    @JsonProperty("number")
    private Integer number;

    /**
     * What to call me.
     */
    @JsonProperty("names")
    private List<Integer> names;

    @JsonProperty("mapping")
    private Map<String, Child> mapping;

}
