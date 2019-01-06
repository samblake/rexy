package rexy.http.response;

import rexy.http.RexyHeader;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

public interface RexyResponse {
	
	int getStatusCode();
	
	String getMimeType();
	
	Collection<RexyHeader> getHeaders();
	
	InputStream getBody();
	
	int getResponseLength();
	
	Map<String, Collection<RexyHeader>> getHeaderMap();
}