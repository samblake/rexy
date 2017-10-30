package rexy.feature.jmx;

import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.OperationsException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static java.beans.Introspector.decapitalize;
import static java.io.File.separator;
import static java.nio.charset.StandardCharsets.UTF_8;

public class JmxRegistry {
	
	private static final JmxRegistry INSTANCE = new JmxRegistry();
	
	private final Map<String, RexyEndpoint> repo = new HashMap<>();
	
	private JmxRegistry() {
	}
	
	public static JmxRegistry getInstance() {
		return INSTANCE;
	}
	
	public RexyEndpoint getResponse(Method method)
			throws OperationsException, MBeanRegistrationException, IOException, URISyntaxException {
		
		String className = method.getDeclaringClass().getSimpleName();
		String methodName = method.getName();
		return getResponse(decapitalize(className) + separator + methodName);
	}
	
	public RexyEndpoint getResponse(String name)
			throws OperationsException, MBeanRegistrationException, IOException, URISyntaxException {
		
		if (!repo.containsKey(name)) {
			synchronized (repo) {
				if (!repo.containsKey(name)) {
					RexyEndpoint response = initResponse(name);
					registerMBean(name, response);
					repo.put(name, response);
					return response;
				}
			}
		}
		
		return repo.get(name);
	}
	
	private RexyEndpoint initResponse(String name) throws IOException, URISyntaxException {
		String path = name + ".json";
		URI uri = getClass().getClassLoader().getResource(path).toURI();
		String contents = new String(Files.readAllBytes(Paths.get(uri)), UTF_8);
		return new RexyEndpoint();
	}
	
	private void registerMBean(String name, RexyEndpoint response)
			throws OperationsException, MBeanRegistrationException {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		ObjectName objectName = new ObjectName(name + ":type=MockResponse");
		server.registerMBean(response, objectName);
	}
}