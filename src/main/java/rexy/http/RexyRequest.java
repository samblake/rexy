package rexy.http;

import java.util.List;

public interface RexyRequest {
	
	String getUri();
	
	String getContextPath();
	
	String getQueryString();
	
	List<RexyHeader> getHeaders();
	
	Method getMethod();
	
}