package rexy.feature.jmx.mock;

public interface MockResponseMBean {
	
	int getHttpStatus();
	
	String getResponse();
	
	void set();
	
}