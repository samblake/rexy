package com.github.samblake.rexy.module.wexy.actions.endpoint;

import com.github.samblake.rexy.config.model.Endpoint;
import com.github.samblake.rexy.module.wexy.Utils;

import javax.management.ObjectInstance;

import static com.github.samblake.rexy.utils.Paths.join;

public class PresetLink {
	private final Endpoint endpoint;
	private final ObjectInstance preset;
	private final String name;
	private final String link;
	
	public PresetLink(Endpoint endpoint, ObjectInstance preset) {
		this.endpoint = endpoint;
		this.preset = preset;
		this.name = preset.getObjectName().getKeyProperty("name");
		this.link = '/' + join(endpoint.getApi().getBaseUrl(), Utils.toUrl(endpoint));
	}
	
	public String getName() {
		return name;
	}
	
	public String getLink() {
		return link;
	}
	
}