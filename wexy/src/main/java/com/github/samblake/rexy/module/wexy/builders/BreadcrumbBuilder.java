package com.github.samblake.rexy.module.wexy.builders;

import com.github.samblake.rexy.config.model.Api;
import com.github.samblake.rexy.config.model.Endpoint;
import com.github.samblake.rexy.module.wexy.model.Breadcrumbs;
import com.github.samblake.rexy.module.wexy.model.Breadcrumbs.Breadcrumb;

import javax.management.ObjectInstance;

import static com.github.samblake.rexy.module.wexy.Utils.toUrl;

public interface BreadcrumbBuilder extends Builder<Breadcrumbs> {
	
	StringBuilder getPath();
	
	abstract class ChildCrumbBuilder implements BreadcrumbBuilder {
		
		private final BreadcrumbBuilder parent;
		
		protected ChildCrumbBuilder(BreadcrumbBuilder parent) {
			this.parent = parent;
		}
		
		@Override
		public Breadcrumbs build() {
			return parent.build().append(new Breadcrumb(getName(), getPath().toString()));
		}
		
		@Override
		public StringBuilder getPath() {
			StringBuilder path = parent.getPath();
			if (path.charAt(path.length()-1) != '/' && getUrlSuffix().charAt(0) != '/') {
				path.append('/');
			}
			return path.append(getUrlSuffix());
		}
		
		protected abstract String getName();
		
		protected abstract String getUrlSuffix();
		
	}
	
	final class HomeCrumbBuilder implements BreadcrumbBuilder {
		
		private final String baseUrl;
		
		public HomeCrumbBuilder(String baseUrl) {
			this.baseUrl = baseUrl;
		}
		
		@Override
		public Breadcrumbs build() {
			return new Breadcrumbs(new Breadcrumb("Home", getPath().toString()));
		}
		
		public ApiCrumbBuilder withApi(Api api) {
			return new ApiCrumbBuilder(this, api);
		}
		
		@Override
		public StringBuilder getPath() {
			return new StringBuilder(baseUrl);
		}
		
		public final class ApiCrumbBuilder extends ChildCrumbBuilder {
			
			private final Api api;
			
			private ApiCrumbBuilder(BreadcrumbBuilder parent, Api api) {
				super(parent);
				this.api = api;
			}
			
			@Override
			protected String getName() {
				return api.getName();
			}
			
			@Override
			protected String getUrlSuffix() {
				return api.getBaseUrl();
			}
			
			public EndpointCrumbBuilder withEndpoint(Endpoint endpoint) {
				return new EndpointCrumbBuilder(this, endpoint);
			}
			
			public final class EndpointCrumbBuilder extends ChildCrumbBuilder {
				
				private final String name;
				private final String urlSuffix;
				
				private EndpointCrumbBuilder(BreadcrumbBuilder parent, Endpoint endpoint) {
					this(parent, endpoint.getName(), toUrl(endpoint));
				}
				
				private EndpointCrumbBuilder(BreadcrumbBuilder parent, String name, String urlSuffix) {
					super(parent);
					this.name = name;
					this.urlSuffix = urlSuffix;
				}
				
				@Override
				protected String getName() {
					return name;
				}
				
				@Override
				protected String getUrlSuffix() {
					return urlSuffix;
				}
				
				public ModuleCrumbBuilder withModule(ObjectInstance objectInstance) {
					String module = objectInstance.getObjectName().getKeyProperty("scope");
					return new ModuleCrumbBuilder(this, module);
				}
				
				final class ModuleCrumbBuilder extends ChildCrumbBuilder {
					private final String name;
					
					private ModuleCrumbBuilder(EndpointCrumbBuilder parent, String name) {
						super(parent);
						this.name = name;
					}
					
					@Override
					protected String getName() {
						return name;
					}
					
					@Override
					protected String getUrlSuffix() {
						return name;
					}
					
				}
				
			}
			
		}
		
	}
	
}