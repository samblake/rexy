package rexy.config;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Map;
import java.util.Set;

public class Headers {

    private Map<String, String> headers;
    
    @JsonAnySetter
    public void add(String key, String value) {
        headers.put(key, value);
    }

    public Set<Map.Entry<String, String>> getHeaders() {
        return headers.entrySet();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("headers", headers).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(headers).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Headers)) {
            return false;
        }
        Headers rhs = ((Headers) other);
        return new EqualsBuilder().append(headers, rhs.headers).isEquals();
    }

}