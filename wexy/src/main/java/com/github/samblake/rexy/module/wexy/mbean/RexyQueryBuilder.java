package com.github.samblake.rexy.module.wexy.mbean;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class RexyQueryBuilder extends MBeanQueryBuilder {
	
	private final String base;
	
	public RexyQueryBuilder() {
		this("Rexy");
	}
	
	public RexyQueryBuilder(String base) {
		this.base = base;
	}
	
	public ApiQueryBuilder withApi(String type) {
		return new ApiQueryBuilder(type);
	}
	
	@Override
	protected StringBuilder getName() {
		return new StringBuilder(base).append(':');
	}
	
	public class ApiQueryBuilder extends MBeanQueryBuilder {
		private final String api;
		
		public ApiQueryBuilder(String api) {
			this.api = api;
		}
		
		public EndpointQueryBuilder withEndpoint(String endpoint) {
			return new EndpointQueryBuilder(endpoint);
		}
		
		@Override
		protected StringBuilder getName() {
			return RexyQueryBuilder.this.getName().append("api=").append(api).append(',');
		}
		
		public class EndpointQueryBuilder extends MBeanQueryBuilder {
			private final String endpoint;
			
			public EndpointQueryBuilder(String endpoint) {
				this.endpoint = endpoint;
			}
			
			public ComponentQueryBuilder withComponent(String component) {
				return new ComponentQueryBuilder(component);
			}
			
			public NameQueryBuilder withName(String name) {
				return new NameQueryBuilder(this, name);
			}
			
			@Override
			protected StringBuilder getName() {
				return ApiQueryBuilder.this.getName().append("endpoint=").append(endpoint).append(',');
			}
			
			public class ComponentQueryBuilder extends MBeanQueryBuilder {
				private final String component;
				
				public ComponentQueryBuilder(String component) {
					this.component = component;
				}
				
				@Override
				protected StringBuilder getName() {
					return EndpointQueryBuilder.this.getName().append("component=").append(component).append(',');
				}
				
				public NameQueryBuilder withName(String name) {
					return new NameQueryBuilder(this, name);
				}
				
			}
			
		}
		
	}
	
	public static class NameQueryBuilder extends MBeanQueryBuilder {
		private final MBeanQueryBuilder parent;
		private final String name;
		
		public NameQueryBuilder(MBeanQueryBuilder parent, String name) {
			this.parent = parent;
			this.name = name;
		}
		
		@Override
		protected StringBuilder getName() {
			return parent.getName().append("name=").append(name);
		}
		
		@Override
		protected ObjectName buildObjectName() throws MalformedObjectNameException {
			return new ObjectName(getName().toString());
		}
	}
	
}