package com.github.samblake.rexy.module.wexy.actions.endpoint;

import com.github.jknack.handlebars.Handlebars;
import com.github.samblake.rexy.config.model.Api;
import com.github.samblake.rexy.config.model.Endpoint;
import com.github.samblake.rexy.http.response.RexyResponse;
import com.github.samblake.rexy.module.wexy.actions.api.EndpointLink;
import com.github.samblake.rexy.module.wexy.builders.BreadcrumbBuilder.HomeCrumbBuilder.ApiCrumbBuilder.EndpointCrumbBuilder;
import com.github.samblake.rexy.module.wexy.mbean.MBeanRepo;
import com.github.samblake.rexy.module.wexy.model.Template;
import com.github.samblake.rexy.module.wexy.model.input.Input;

import javax.management.MBeanInfo;
import javax.management.ObjectInstance;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static com.github.samblake.rexy.module.wexy.actions.endpoint.InputFactory.createInput;

public class EndpointAction extends AbstractEndpointAction {
	
	protected final MBeanRepo mBeanRepo = new MBeanRepo();
	private final String rexyLocation;
	
	public EndpointAction(String baseUrl, Handlebars handlebars, String rexyLocation) {
		super(baseUrl, handlebars);
		this.rexyLocation = rexyLocation;
	}
	
	@Override
	protected RexyResponse perform(Api api, Endpoint endpoint,
			Map<String, String> params, EndpointCrumbBuilder crumbBuilder) {
		
		try {
			Template template = createTemplate("endpoint", crumbBuilder);
			return createResponse(template, api, endpoint, "mock");
		}
		catch (IOException e) {
			throw new RuntimeException("Could perform request", e);
		}
	}
	
	protected RexyResponse createResponse(Template template,
			Api api, Endpoint endpoint, String activeModule) throws IOException {
		
		return createResponse(template
				.with("tabs", findTabs(api, endpoint, activeModule))
				.with("uri", generateUri(rexyLocation, api, endpoint)));
	}
	
	protected List<Tab<Module>> findTabs(Api api, Endpoint endpoint, String activeModule) {
		Set<ObjectInstance> results = mBeanRepo.findForEndpoint(api, endpoint);
		
		return results.stream()
				.filter(EndpointObjectInstances::isModule)
				.map(oi -> createTab(oi, results, endpoint, activeModule))
				.collect(toList());
	}
	
	private Tab<Module> createTab(ObjectInstance moduleObjectInstance,
                                  Set<ObjectInstance> objectInstances, Endpoint endpoint, String activeModule) {
		
		Tab<Module> tab = createTab(moduleObjectInstance, objectInstances, endpoint);
		
		if (tab.getId().equals(activeModule)) {
			tab.setActive();
		}
		
		return tab;
	}
	
	private Tab<Module> createTab(ObjectInstance moduleObjectInstance,
                                  Set<ObjectInstance> objectInstances, Endpoint endpoint) {
		
		String contentType = endpoint.getApi().getContentType();
		MBeanInfo info = mBeanRepo.getInfo(moduleObjectInstance);
		List<Input> inputs = stream(info.getAttributes())
				.map(attributeInfo -> createInput(moduleObjectInstance, attributeInfo, contentType,
						mBeanRepo.getAttributeValue(moduleObjectInstance, attributeInfo)))
				.collect(toList());
		
		String name = moduleObjectInstance.getObjectName().getKeyProperty("name");
		
		if (EndpointObjectInstances.isMock(moduleObjectInstance)) {
			return createMockTab(moduleObjectInstance, objectInstances, endpoint, name, info, inputs);
		}
		
		String action = new EndpointLink(endpoint).getLink();
		return new Tab<>(name, new Module(moduleObjectInstance, info, inputs, action));
	}
	
	private Tab<Module> createMockTab(ObjectInstance moduleObjectInstance, Set<ObjectInstance> objectInstances,
                                      Endpoint endpoint, String name, MBeanInfo info, List<Input> inputs) {
		
		List<PresetLink> presets = objectInstances.stream()
				.filter(EndpointObjectInstances::isPreset)
				.map(oi -> new PresetLink(endpoint, oi))
				.collect(toList());
		
		String action = new EndpointLink(endpoint).getLink();
		MockModule module = new MockModule(moduleObjectInstance, info, inputs, action, presets);
		return new Tab<>(name, module);
	}
	
	private Uri generateUri(String domain, Api api, Endpoint endpoint) {
		String path = baseUrl + api.getBaseUrl() + endpoint.getEndpoint();
		return Uri.fromUrl(endpoint.getMethod(), domain, path);
	}
	
}