package com.github.samblake.rexy.module.wexy.actions.api;

import com.github.samblake.rexy.config.model.Endpoint;
import com.github.samblake.rexy.module.wexy.Utils;

import static com.github.samblake.rexy.utils.Paths.join;

public class EndpointLink {
	private final Endpoint endpoint;
	private final String link;
	
	public EndpointLink(Endpoint endpoint) {
		this.endpoint = endpoint;
		this.link = join("/", endpoint.getApi().getBaseUrl(), Utils.toUrl(endpoint));
	}
	
	public Endpoint getEndpoint() {
		return endpoint;
	}
	
	public String getLink() {
		return link;
	}

}