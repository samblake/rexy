package rexy.feature.jmx;

public class RexyEndpoint implements RexyEndpointMBean {
	
	private boolean intercept;
	private String contentType;
	private int httpStatus;
	private String response;
	
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
	public String getResponse() {
		return response;
	}
	
	@Override
	public void setResponse(String response) {
		this.response = response;
	}

}