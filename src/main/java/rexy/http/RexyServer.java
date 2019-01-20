package rexy.http;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.RexyConfig;
import rexy.config.model.Api;
import rexy.http.response.RexyResponse;
import rexy.module.RexyModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static fi.iki.elonen.NanoHTTPD.Method.POST;
import static fi.iki.elonen.NanoHTTPD.Method.PUT;
import static java.util.Collections.emptyList;
import static rexy.http.response.BasicRexyResponse.errorResponse;
import static rexy.utils.Paths.join;

/**
 * The Rexy HTTP server. Listens for incoming requests and routes them to handlers for each API.
 */
public class RexyServer extends NanoHTTPD {
	private static final Logger logger = LogManager.getLogger(RexyServer.class);
	
	protected final Map<String, RexyHandler> routes = new HashMap<>();
	protected final String baseUrl;
	private final List<RexyModule> modules;
	
	/**
	 * Creates a server and initialises the modules for each API.
	 *
	 * @param config The Rexy config
	 * @param modules The enabled modules
	 */
	public RexyServer(RexyConfig config, List<RexyModule> modules) {
		this(config.getPort(), config.getBasePath(), modules, config.getApis());
	}
	
	/**
	 * Creates a server. APIs should be initialised by calling {@link #initApi(Api)}.
	 *
	 * @param port The port to run the server on
	 * @param baseUrl The base URL of the service
	 * @param modules The enabled modules
	 */
	public RexyServer(int port, String baseUrl, List<RexyModule> modules) {
		this(port, baseUrl, modules, emptyList());
	}
	
	/**
	 * Creates a server and initialises the modules for each API.
	 *
	 * @param port The port to run the server on
	 * @param baseUrl The base URL of the service
	 * @param modules The enabled modules
	 * @param apis The API registered with Rexy
	 */
	public RexyServer(int port, String baseUrl, List<RexyModule> modules, List<Api> apis) {
		super(port);
		this.baseUrl = baseUrl;
		this.modules = modules;
		
		for (Api api : apis) {
			initApi(api);
		}
	}
	
	public void initApi(Api api) {
		String apiEndpoint = join(baseUrl, api.getBaseUrl());
		routes.put(apiEndpoint, new RexyHandler(api, modules));
		logger.info("API endpoint created for " + apiEndpoint);
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
			if (session.getMethod() == POST || session.getMethod() == PUT) {
				// If we don't parse the body form parameters are not made available
				session.parseBody(new HashMap<>());
			}
			
			NanoRequest request = new NanoRequest(session, route);
			
			return handler.handle(request).orElseGet(() -> errorResponse(501,
					"No API endpoint registered for %s %s", session.getUri(), session.getMethod()));
		}
		catch (IOException | ResponseException e) {
			logger.error("Error processing request", e);
			return errorResponse(500, "Error processing request");
		}
	}
	
	protected Response createRespone(RexyResponse rexyResponse) {
		int responseLength = rexyResponse.getResponseLength();
		InputStream body = rexyResponse.getBody();
		Status status = Status.lookup(rexyResponse.getStatusCode());
		Response response = newFixedLengthResponse(status, rexyResponse.getMimeType(), body, responseLength);
		rexyResponse.getHeaders().forEach(header -> response.addHeader(header.getName(), header.getValue()));
		return response;
	}
	
	@Override
	public void start() throws IOException {
		start(SOCKET_READ_TIMEOUT, false);
	}

}