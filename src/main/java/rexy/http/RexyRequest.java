package rexy.http;

import java.util.List;
import java.util.Map;

public interface RexyRequest {
	
	String getUri();
	
	String getContextPath();
	
	String getQueryString();
	
	List<RexyHeader> getHeaders();
	
	Method getMethod();
	
	Map<String, String> getParameters();
}