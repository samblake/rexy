package rexy.http;

public enum Method {
	GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, TRACE, CONNECT;
	
	public static Method from(String name) {
		return name == null ? GET : valueOf(name.toUpperCase());
	}
}