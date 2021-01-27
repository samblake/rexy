package com.github.samblake.rexy.module.wexy;

import com.github.jknack.handlebars.Handlebars;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.github.samblake.rexy.http.NanoRequest;
import com.github.samblake.rexy.http.RexyHandler;
import com.github.samblake.rexy.http.RexyServer;
import com.github.samblake.rexy.module.RexyModule;
import com.github.samblake.rexy.module.wexy.actions.index.IndexAction;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static com.github.jknack.handlebars.helper.ConditionalHelpers.eq;
import static fi.iki.elonen.NanoHTTPD.Response.Status.OK;
import static com.github.samblake.rexy.http.Method.GET;

public class WexyServer extends RexyServer {
	private static final Logger logger = LogManager.getLogger(WexyServer.class);
	
	private final IndexAction indexAction;
	private final Handlebars handlebars;
	
	public WexyServer(int port, String baseUrl, List<RexyModule> modules) {
		super(port, baseUrl, modules);
		this.handlebars = new Handlebars();
		handlebars.registerHelper(eq.name(), eq);
		
		indexAction = new IndexAction(baseUrl, handlebars, routes);
	}
	
	public Map<String, RexyHandler> getRoutes() {
		return routes;
	}
	
	public Handlebars getHandlebars() {
		return handlebars;
	}
	
	@Override
	public Response serve(IHTTPSession session) {
		String url = session.getUri(); // TODO remove with base URL
		
		// Handle static files
		if (url.endsWith(".css") || url.endsWith(".js")) {
			InputStream resource = session.getClass().getResourceAsStream(url);
			if (resource != null) {
				logger.debug("Serving " + url);
				String mineType = url.endsWith(".css") ? "text/css" : "application/javascript";
				return newChunkedResponse(OK, mineType, resource);
			}
		}
		
		// Handle index
		if (url.equalsIgnoreCase(baseUrl)) {
			NanoRequest request = new NanoRequest(session, "/");
			if (request.getMethod() == GET) {
				return createRespone(indexAction.perform());
			}
		}
		
		// All other business
		return super.serve(session);
	}
	
}