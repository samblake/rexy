package com.github.samblake.rexy.module.wexy;

import com.codepoetics.ambivalence.Either;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.jknack.handlebars.Handlebars;
import com.github.samblake.rexy.Rexy.RexyDetails;
import com.github.samblake.rexy.config.model.Api;
import com.github.samblake.rexy.http.RexyHandler;
import com.github.samblake.rexy.http.request.RexyRequest;
import com.github.samblake.rexy.http.response.RexyResponse;
import com.github.samblake.rexy.module.ModuleAdapter;
import com.github.samblake.rexy.module.wexy.actions.WexyAction;
import com.github.samblake.rexy.module.wexy.actions.api.ApiAction;
import com.github.samblake.rexy.module.wexy.actions.endpoint.EndpointAction;
import com.github.samblake.rexy.module.wexy.actions.module.UpdateModuleAction;
import jauter.Routed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import static com.codepoetics.ambivalence.Either.ofRight;
import static com.github.samblake.rexy.http.Method.GET;
import static com.github.samblake.rexy.http.Method.POST;
import static com.github.samblake.rexy.utils.Json.intValue;
import static com.github.samblake.rexy.utils.Json.stringValue;
import static java.util.Collections.singletonList;

public class WexyModule extends ModuleAdapter {
	private static final Logger logger = LogManager.getLogger(WexyModule.class);
	
	private WexyServer wexyServer;
	private String baseUrl;
	
	@Override
	public void init(RexyDetails rexyDetails, JsonNode config) {
		this.baseUrl = stringValue(config, "baseUrl", "/");
		Integer port = intValue(config, "port", 8090);
		InternalModule internalModule = new InternalModule();
		this.wexyServer = new WexyServer(port, baseUrl, singletonList(internalModule));
		internalModule.init(rexyDetails, config);
		
		try {
			wexyServer.start();
			logger.info("Wexy starterd on port " + port);
		}
		catch (IOException e) {
			throw new RuntimeException("Could not start Wexy server", e);
		}
	}
	
	@Override
	public void initEndpoint(Api api) {
		wexyServer.initApi(api);
	}
	
	@Override
	public Either<RexyRequest, RexyResponse> handleRequest(Api api, RexyRequest request) {
		return Either.ofLeft(request);
	}
	
	private final class InternalModule extends ModuleAdapter {
		
		private final WexyRouter router = new WexyRouter();
		
		@Override
		public void init(RexyDetails rexyDetails, JsonNode config) {
			Handlebars handlebars = wexyServer.getHandlebars();
			Map<String, RexyHandler> apiRoutes = wexyServer.getRoutes();
			
			String rexyLocation = getRexyLocation(rexyDetails, config);
			
			router.pattern(GET, "/:api", new ApiAction(baseUrl, handlebars, apiRoutes));
			router.pattern(GET, "/:api/:endpoint", new EndpointAction(baseUrl, handlebars, rexyLocation));
			router.pattern(POST, "/:api/:endpoint", new UpdateModuleAction(baseUrl, handlebars, rexyLocation));
		}
		
		private String getRexyLocation(RexyDetails rexyDetails, JsonNode config) {
			String hostname = stringValue(config, "rexy", this::getDefaultHostname);
			String baseUrl = rexyDetails.getBaseUrl().startsWith("/")
					? rexyDetails.getBaseUrl().substring(1): rexyDetails.getBaseUrl();
			
			return String.format("http://%s:%s%s", hostname, rexyDetails.getPort(), baseUrl);
		}
		
		private String getDefaultHostname() {
			logger.info("Rexy hostname not supplied");
			try {
				String hostName = InetAddress.getLocalHost().getHostName();
				logger.info("Using " + hostName + " as Rexy hostname");
				return hostName;
			}
			catch (UnknownHostException e) {
				logger.warn("Not able to detect hostname, using localhost");
				return "localhost";
			}
		}
		
		@Override
		public Either<RexyRequest, RexyResponse> handleRequest(Api api, RexyRequest request) {
			Routed<WexyAction> route = router.route(request.getMethod(), request.getUri());
			
			if (route.notFound()) {
				throw new RuntimeException("No route found for " + request.getUri());
			}
			else {
				Map<String, String> params = combineParams(request, route);
				return ofRight(route.target().perform(api, params));
			}
		}
		
		private Map<String, String> combineParams(RexyRequest request, Routed<WexyAction> route) {
			Map<String, String> params = new HashMap<>();
			params.putAll(route.params());
			params.putAll(request.getParameters());
			return params;
		}
		
	}
	
}