package com.github.samblake.rexy.module.wexy;

import com.github.jknack.handlebars.Handlebars;
import com.github.samblake.rexy.http.NanoRequest;
import com.github.samblake.rexy.http.RexyHandler;
import com.github.samblake.rexy.http.RexyServer;
import com.github.samblake.rexy.module.RexyModule;
import com.github.samblake.rexy.module.wexy.actions.index.IndexAction;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import static com.github.jknack.handlebars.helper.ConditionalHelpers.eq;
import static com.github.samblake.rexy.http.Method.GET;
import static fi.iki.elonen.NanoHTTPD.Response.Status.INTERNAL_ERROR;
import static fi.iki.elonen.NanoHTTPD.Response.Status.OK;
import static java.lang.System.lineSeparator;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;

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

	@SuppressFBWarnings(
			value = {
					"NP_LOAD_OF_KNOWN_NULL_VALUE",
					"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE",
					"RCN_REDUNDANT_NULLCHECK_OF_NULL_VALUE"
			},
			justification = "Seems to be a false positive - https://github.com/spotbugs/spotbugs/issues/1338"
	)
	@Override
	public Response serve(IHTTPSession session) {
		String url = session.getUri(); // TODO remove with base URL
		
		// Handle static files
		if (url.endsWith(".css") || url.endsWith(".js")) {
			try (InputStream resource = session.getClass().getResourceAsStream(url)) {
				if (resource != null) {
					logger.debug("Serving " + url);
					String mineType = url.endsWith(".css") ? "text/css" : "application/javascript";
					
					// return newChunkedResponse(OK, mineType, resource);
					// FIXME chunked response stopped working, fall back on reading file to String as below
					
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource, UTF_8))) {
						String file = reader.lines().collect(joining(lineSeparator()));
						return newFixedLengthResponse(OK, mineType, file);
					}
				}
			}
			catch (IOException e) {
				logger.error("Unable to serve static resource " + url, e);
				return newFixedLengthResponse(INTERNAL_ERROR, null, null);
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