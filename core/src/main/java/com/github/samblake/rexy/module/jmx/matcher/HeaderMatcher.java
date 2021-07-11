package com.github.samblake.rexy.module.jmx.matcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.samblake.rexy.http.RexyHeader;
import com.github.samblake.rexy.http.request.RexyRequest;
import com.github.samblake.rexy.module.jmx.JmxRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map.Entry;

/**
 * <p>Matches requests that contain a specific header. The header name and value should be supplied as a
 * key-value pair in the configuration. For example:
 *
 * <pre><code>{@code
 * "matchers": {
 *   "header": {
 *      "Accept": "application/xhtml+xml"
 *   }
 * },
 * }</code></pre>
 *
 * @param <T> The type of MBean
 */
public final class HeaderMatcher<T> extends AbstractRequestMatcher<T> {
	private static final Logger logger = LogManager.getLogger(JmxRegistry.class);
	
	private final RexyHeader header;
	
	private HeaderMatcher(RexyHeader header, T mBean) {
		super(mBean);
		this.header = header;
	}
	
	static <T> RequestMatcher<T> create(JsonNode config, T mBean) {
		if (!config.isObject()) {
			throw new RuntimeException("Invalid config, object expected");
		}
		
		if (config.size() != 1) {
			throw new RuntimeException("Invalid config, single child expected");
		}
		
		Entry<String, JsonNode> entry = config.fields().next();
		if (!entry.getValue().isTextual()) {
			throw new RuntimeException("Invalid header value, text expected");
		}
		
		RexyHeader header = new RexyHeader(entry.getKey(), entry.getValue().asText());
		
		return new HeaderMatcher<>(header, mBean);
	}
	
	@Override
	public boolean matches(RexyRequest request) {
		if (logger.isDebugEnabled()) {
			logger.debug("Testing header " + header);
		}
		
		return request.getHeaders().stream().anyMatch(header -> header.equals(this.header));
	}
	
	@Override
	public String toString() {
		return getMBean().getClass().getSimpleName() + ':' + header;
	}
	
}