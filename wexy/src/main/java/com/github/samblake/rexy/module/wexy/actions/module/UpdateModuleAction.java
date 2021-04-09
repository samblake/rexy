package com.github.samblake.rexy.module.wexy.actions.module;

import com.github.jknack.handlebars.Handlebars;
import com.github.samblake.rexy.config.model.Api;
import com.github.samblake.rexy.config.model.Endpoint;
import com.github.samblake.rexy.http.response.RexyResponse;
import com.github.samblake.rexy.module.wexy.actions.endpoint.EndpointAction;
import com.github.samblake.rexy.module.wexy.builders.BreadcrumbBuilder.HomeCrumbBuilder.ApiCrumbBuilder.EndpointCrumbBuilder;
import com.github.samblake.rexy.module.wexy.model.Template;

import javax.management.JMException;
import java.io.IOException;
import java.util.Map;

import static com.github.samblake.rexy.module.wexy.model.Notification.failure;
import static com.github.samblake.rexy.module.wexy.model.Notification.success;

public class UpdateModuleAction extends EndpointAction {
	
	public UpdateModuleAction(String baseUrl, Handlebars handlebars, String rexyLocation) {
		super(baseUrl, handlebars, rexyLocation);
	}
	
	@Override
	protected RexyResponse perform(Api api, Endpoint endpoint,
			Map<String, String> params, EndpointCrumbBuilder crumbBuilder) {
		
		String moduleName = params.get("module");
		String presetName = params.get("preset");
		
		try {
			Template template = createTemplate("endpoint", crumbBuilder);
			
			if (presetName != null) {
				applyPreset(endpoint, presetName, template);
			}
			else if (params.size() > 3) {
				applyUpdate(endpoint, params, moduleName, template);
			}
			
			return createResponse(template, api, endpoint, moduleName);
		}
		catch (IOException e) {
			throw new RuntimeException("Could perform request", e);
		}
	}
	
	private void applyPreset(Endpoint endpoint, String presetName, Template template) {
		try {
			mBeanRepo.applyPreset(endpoint, presetName);
			
			template.withNotification(success("The %s preset has been applied", presetName));
		}
		catch (JMException e) {
			template.withNotification(failure("The %s preset could not be applied: %s", presetName, e.getMessage()));
		}
	}
	
	private void applyUpdate(Endpoint endpoint, Map<String, String> params, String moduleName, Template template) {
		try {
			mBeanRepo.updateValues(endpoint, moduleName, params);
			
			template.withNotification(success("The %s module has been updated", moduleName));
		}
		catch (JMException e) {
			template.withNotification(failure("The %s module could not be updated: %s", moduleName, e.getMessage()));
		}
	}
	
}