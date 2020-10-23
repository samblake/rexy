package rexy.module.jmx.mock;

import com.fasterxml.jackson.databind.JsonNode;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.log4j.Logger;
import rexy.config.ConfigException;
import rexy.config.model.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * The mock endpoint implementation for the {@link MockModule}.
 */
public class MockEndpoint implements MockEndpointMBean {
	private static final Logger logger = Logger.getLogger(MockEndpoint.class);
	
	private boolean intercept;
	private String contentType;
	private int httpStatus;
	private String body;
	private Map<String, String> headers;
	
	public MockEndpoint(String contentType, Response response, boolean intercept) {
		this(contentType, response.getHttpStatus(), response.getHeaders(), getBody(response), intercept);
	}
	
	public MockEndpoint(String contentType, int httpStatus,
			Map<String, String> headers, String body, boolean intercept) {
		
		this.httpStatus = httpStatus;
		this.contentType = contentType;
		this.headers = headers;
		this.body = body;
		this.intercept = intercept;
	}
	
	@Override
	public boolean isIntercept() {
		return intercept;
	}
	
	@Override
	public void setIntercept(boolean intercept) {
		this.intercept = intercept;
	}
	
	@Override
	public String getContentType() {
		return contentType;
	}
	
	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	@Override
	public int getHttpStatus() {
		return httpStatus;
	}
	
	@Override
	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}
	
	@Override
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	@Override
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	@Override
	public String getBody() {
		return body;
	}
	
	@Override
	public void setBody(String body) {
		this.body = body;
	}
	
	private static String getBody(Response response) {
		return response.getBody().map(MockEndpoint::findBody).orElse(null);
	}
	
	private static String findBody(JsonNode node) {
		try {
			return node.isTextual() ? parse(node.asText()) : node.toPrettyString();
		}
		catch (ConfigException e) {
			// FIXME improve error handling, the whole method probably need moving out of here
			throw new RuntimeException("Could not load config", e);
		}
	}
	
	@SuppressFBWarnings(
			value = {
					"NP_LOAD_OF_KNOWN_NULL_VALUE",
					"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE",
					"RCN_REDUNDANT_NULLCHECK_OF_NULL_VALUE"
			},
			justification = "Seems to be a false positive - https://github.com/spotbugs/spotbugs/issues/1338"
	)
	private static String parse(String path) throws ConfigException {
		logger.info("Attempting to read body from " + path);
		try {
			try (InputStream inputStream = MockEndpoint.class.getResourceAsStream('/' + path)) {
				if (inputStream != null) {
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8))) {
						return reader.lines().collect(Collectors.joining("\n"));
					}
				}
			}
			
			logger.debug(path + " not found on classpath, looking for absolute file");
			File file = new File(path);
			if (file.exists()) {
				try (BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(file), UTF_8))) {
					return reader.lines().collect(Collectors.joining("\n"));
				}
			}
			
			logger.debug(path + " absolute file not found, using string value");
			return path;
		}
		catch (IOException e) {
			throw new ConfigException("Could not read " + path, e);
		}
	}
	
}