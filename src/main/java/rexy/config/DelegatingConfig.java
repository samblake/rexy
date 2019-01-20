package rexy.config;

import com.fasterxml.jackson.databind.JsonNode;
import rexy.config.model.Api;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * An abstract class that delegates all it's calls to another {@link RexyConfig}.
 */
public abstract class DelegatingConfig implements RexyConfig {
	
	private final RexyConfig delegate;
	
	protected DelegatingConfig(RexyConfig delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public int getPort() {
		return delegate.getPort();
	}
	
	@Override
	public String getBasePath() {
		return delegate.getBasePath();
	}
	
	@Override
	public List<String> getScanPackages() {
		return delegate.getScanPackages();
	}
	
	@Override
	public LinkedHashMap<String, JsonNode> getModules() {
		return delegate.getModules();
	}
	
	@Override
	public List<Api> getApis() {
		return delegate.getApis();
	}

}