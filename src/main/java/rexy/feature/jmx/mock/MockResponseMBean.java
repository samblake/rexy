package rexy.feature.jmx.mock;

import java.util.Map;

public interface MockResponseMBean {
	
	int getHttpStatus();
	
	Map<String, String> getHeaders();
	
	String getBody();
	
	void set();
	
}