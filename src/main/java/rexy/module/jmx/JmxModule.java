package rexy.module.jmx;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.model.Api;
import rexy.config.model.Endpoint;
import rexy.http.RexyRequest;
import rexy.http.RexyResponse;
import rexy.module.ModuleAdapter;
import rexy.module.ModuleInitialisationException;

import javax.management.JMException;
import java.util.Optional;

import static java.util.Optional.empty;

/**
 * A module for registering MBeans against the endpoints of an API.
 *
 * @param <T> The type of MBean
 */
public abstract class JmxModule<T> extends ModuleAdapter {
	private static final Logger logger = LogManager.getLogger(JmxModule.class);
	
	private JmxRegistry<T> registry;
	
	@Override
	public void init(JsonNode config) throws ModuleInitialisationException {
		registry = createRegistry(config);
	}
	
	/**
	 * Gets the registry to store the MBean in.
	 *
	 * @param config The module configuration
	 * @return The MBean registry
	 */
	protected abstract JmxRegistry<T> createRegistry(JsonNode config);
	
	@Override
	public void initEndpoint(Api api) throws ModuleInitialisationException {
		for (Endpoint endpoint : api.getEndpoints()) {
			try {
				registry.addEndpoint(endpoint);
			}
			catch (JMException e) {
				String message = "Could not create endpoint for " + api.getBaseUrl() + endpoint.getEndpoint();
				throw new ModuleInitialisationException(this, message, e);
			}
		}
	}
	
	@Override
	public Optional<RexyResponse> handleRequest(Api api, RexyRequest request) {
		Optional<T> mBean = findEndpointMBean(request);
		
		if (mBean.isPresent()) {
			return handleRequest(api, request, mBean.get());
		}
		else {
			logger.warn("No match for " + request.getUri());
			return empty();
		}
	}
	
	private Optional<T> findEndpointMBean(RexyRequest exchange) {
		String query = exchange.getQueryString();
		String request = exchange.getUri() + (query == null ? "" : '?' + query);
		return registry.getMBean(request);
	}
	
	/**
	 * Handles the request when an MBean can be found for the endpoint.
	 *
	 * @param api      The API the request is against
	 * @param request  The request to process
	 * @param mBean    The MBean associated with the request
	 * @return True if the response has been written, false otherwise
	 */
	protected abstract Optional<RexyResponse> handleRequest(Api api, RexyRequest request, T mBean);
	
}