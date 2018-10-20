package rexy.http;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.model.Api;
import rexy.config.model.Config;
import rexy.module.Module;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static rexy.http.RexyResponse.errorResponse;
import static rexy.utils.Paths.join;

public class RexyServer extends NanoHTTPD {
	private static final Logger logger = LogManager.getLogger(RexyServer.class);
	
	private final Map<String, RexyHandler> routes = new HashMap<>();
	
	/**
	 * Creates a server and initialises the modules for each API.
	 *
	 * @param config  The configuration containing the port, base URL and APIs
	 * @param modules The modules to register with the endpoints
	 */
	public RexyServer(Config config, List<Module> modules) {
		super(config.getPort());
		for (Api api : config.getApis()) {
			String apiEndpoint = join(config.getBaseUrl(), api.getBaseUrl());
			routes.put(apiEndpoint, new RexyHandler(api, modules));
			logger.info("API endpoint created for " + apiEndpoint);
		}
	}
	
	@Override
	public Response serve(IHTTPSession session) {
		
		Optional<Entry<String, RexyHandler>> route = routes.entrySet().stream()
				.filter(entry -> session.getUri().startsWith(entry.getKey()))
				.findFirst();
		
		RexyResponse response = route.map(r -> performRequest(session, r.getKey(), r.getValue()))
				.orElseGet(() -> errorResponse(404, "No API registered for %s", session.getUri()));
		
		return createRespone(response);
	}
	
	private RexyResponse performRequest(IHTTPSession session, String route, RexyHandler handler) {
		try {
			return handler.handle(new NanoRequest(session, route))
					.orElseGet(() -> errorResponse(501,
							"No API endpoint registered for %s %s", session.getUri(), session.getMethod()));
		}
		catch (IOException e) {
			logger.error("Error processing request", e);
			return errorResponse(500, "Error processing request");
		}
	}
	
	private Response createRespone(RexyResponse rexyResponse) {
		int responseLength = rexyResponse.getBody().length;
		ByteArrayInputStream input = new ByteArrayInputStream(rexyResponse.getBody());
		Status status = Status.lookup(rexyResponse.getStatusCode());
		Response response = newFixedLengthResponse(status, rexyResponse.getMimeType(), input, responseLength);
		rexyResponse.getHeaders().forEach(h -> response.addHeader(h.getName(), h.getValue()));
		return response;
	}
	
	@Override
	public void start() throws IOException {
		start(SOCKET_READ_TIMEOUT, false);
	}

}