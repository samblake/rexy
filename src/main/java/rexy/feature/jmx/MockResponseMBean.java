package rexy.feature.jmx;

public interface MockResponseMBean {
	
	int getHttpStatus();
	
	String getResponse();
	
	void set();
	
}