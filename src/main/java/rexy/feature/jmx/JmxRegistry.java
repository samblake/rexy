package rexy.feature.jmx;

import org.apache.commons.lang.StringUtils;
import rexy.config.model.Api;
import rexy.config.model.Endpoint;
import rexy.config.model.Response;

import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.OperationsException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isEmpty;

public final class JmxRegistry {
	
	private static final JmxRegistry INSTANCE = new JmxRegistry();
	
	private final Map<Pattern, MockEndpoint> repo = new HashMap<>();
	
	private JmxRegistry() {
	}
	
	public static JmxRegistry getInstance() {
		return INSTANCE;
	}
	
	public void addEndpoint(Api api, Endpoint endpoint) throws OperationsException, MBeanRegistrationException {
		List<Response> responses = endpoint.getResponses();
		Response defaultResponse = responses.iterator().next();
		MockEndpoint mockEndpoint = new MockEndpoint(api.getContentType(), defaultResponse);
		registerMBean(api.getName(), endpoint.getName(), mockEndpoint);
		int i = 0;
		for (Response response : responses) {
			String name = isEmpty(response.getName()) ? Integer.toString(i++) : response.getName();
			MockResponse mockResponse = new MockResponse(mockEndpoint, response);
			registerMBean(api.getName(), endpoint.getName(), mockResponse, name);
		}
		repo.put(createRegex(api, endpoint), mockEndpoint);
	}
	
	private void registerMBean(String type, String name, MockEndpoint endpoint)
			throws OperationsException, MBeanRegistrationException {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		ObjectName objectName = new ObjectName("Rexy:type=" + type + ",name=" + name);
		server.registerMBean(endpoint, objectName);
	}
	
	private void registerMBean(String type, String name, MockResponse response, String presetName)
			throws OperationsException, MBeanRegistrationException {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		ObjectName objectName = new ObjectName("Rexy:type=" + type + ",scope=" + name + ",name=preset-" + presetName);
		server.registerMBean(response, objectName);
	}
	
	private Pattern createRegex(Api api, Endpoint endpoint) {
		Pattern pattern = Pattern.compile("\\{.+?\\}");
		Matcher matcher = pattern.matcher(escape(endpoint.getEndpoint()));
		String regex = ".*" + api.getBaseUrl() + matcher.replaceAll(".+?");
		return Pattern.compile(regex);
	}
	
	private String escape(String endpoint) {
		// TODO better escaping
		return endpoint.replace("?", "\\?");
	}
	
	public MockEndpoint getEndpoint(String path) {
		for (Entry<Pattern, MockEndpoint> entry : repo.entrySet()) {
			if (entry.getKey().matcher(path).matches()) {
				return entry.getValue();
			}
		}
		return null;
	}
}