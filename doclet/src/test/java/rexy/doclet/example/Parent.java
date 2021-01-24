package rexy.doclet.example;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * <p>And <i>I</i> am the...</p>
 * <p><b>{@code
 * parent
 * }!</b></p>
 */
public class Parent extends GrandParent implements Inter {

    private Child child;

    /**
     * What to call me.
     */
    @JsonProperty("names")
    private List<Integer> names;

    @JsonProperty("mapping")
    private Child mapping;

}
