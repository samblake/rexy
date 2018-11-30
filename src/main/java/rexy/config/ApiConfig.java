package rexy.config;

import rexy.config.model.Api;

import java.util.ArrayList;
import java.util.List;

/**
 * Takes a config and a list of additional APIs. Delegates to the config for all calls except for {@link #getApis()}
 * in which case it returns a list of APIs created on construction from the APIs returned by the delegate at the time
 * as well as the additional APIs.
 */
public class ApiConfig extends DelegatingConfig {
	
	private final List<Api> apis;
	
	public ApiConfig(RexyConfig delegate, List<Api> additionalApis) {
		super(delegate);
		
		List<Api> apis = new ArrayList<>();
		apis.addAll(delegate.getApis());
		apis.addAll(additionalApis);
		this.apis = apis;
	}
	
	@Override
	public List<Api> getApis() {
		return apis;
	}

}