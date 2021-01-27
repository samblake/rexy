package com.github.samblake.rexy.module.wexy.actions.endpoint;

import com.github.jknack.handlebars.Handlebars;
import com.github.samblake.rexy.config.model.Api;
import com.github.samblake.rexy.config.model.Endpoint;
import com.github.samblake.rexy.http.response.RexyResponse;
import com.github.samblake.rexy.module.wexy.Utils;
import com.github.samblake.rexy.module.wexy.actions.api.AbstractApiAction;
import com.github.samblake.rexy.module.wexy.builders.BreadcrumbBuilder.HomeCrumbBuilder.ApiCrumbBuilder;
import com.github.samblake.rexy.module.wexy.builders.BreadcrumbBuilder.HomeCrumbBuilder.ApiCrumbBuilder.EndpointCrumbBuilder;
import com.github.samblake.rexy.module.wexy.mbean.MBeanRepo;

import java.util.Map;

public abstract class AbstractEndpointAction extends AbstractApiAction {
	
	private final MBeanRepo mBeanRepo = new MBeanRepo();
	
	public AbstractEndpointAction(String baseUrl, Handlebars handlebars) {
		super(baseUrl, handlebars);
	}
	
	@Override
	protected RexyResponse perform(Api api, Map<String, String> params, ApiCrumbBuilder crumbBuilder) {
		String endpointName = params.get("endpoint");
		
		Endpoint endpoint = api.getEndpoints().stream()
				.filter(e -> Utils.toUrl(e).equals(endpointName))
				.findFirst().orElseThrow(() -> new RuntimeException("Unknown endpoint " + endpointName));
		
		return perform(api, endpoint, params, crumbBuilder.withEndpoint(endpoint));
	}
	
	protected abstract RexyResponse perform(Api api, Endpoint endpoint,
			Map<String, String> params, EndpointCrumbBuilder crumbBuilder);
	
}