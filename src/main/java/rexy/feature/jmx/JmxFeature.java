package rexy.feature.jmx;

import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.model.Api;
import rexy.config.model.Endpoint;
import rexy.feature.FeatureAdapter;
import rexy.feature.FeatureInitialisationException;

import javax.management.JMException;
import java.util.Map;

/**
 * A feature for registering MBeans against the endpoints of an API.
 *
 * @param <T> The type of MBean
 */
public abstract class JmxFeature<T> extends FeatureAdapter {
	private static final Logger logger = LogManager.getLogger(JmxFeature.class);
	
	private JmxRegistry<T> registry;
	
	@Override
	public void init(Map<String, Object> jsonNode) throws FeatureInitialisationException {
		registry = getRegistry();
	}
	
	/**
	 * Gets the registry to store the MBean in.
	 *
	 * @return The MBean registry
	 */
	protected abstract JmxRegistry<T> getRegistry();
	
	@Override
	public void initEndpoint(Api api) throws FeatureInitialisationException {
		for (Endpoint endpoint : api.getEndpoints()) {
			try {
				registry.addEndpoint(api, endpoint);
			}
			catch (JMException e) {
				String message = "Could not create endpoint for " + api.getBaseUrl() + endpoint.getEndpoint();
				throw new FeatureInitialisationException(this, message, e);
			}
		}
	}
	
	@Override
	public boolean handleRequest(Api api, HttpExchange exchange) {
		T mBean = findEndpointMBean(exchange);
		
		if (mBean != null) {
			if (handleRequest(api, exchange, mBean)) {
				return true;
			}
		}
		else {
			logger.warn("No match for " + exchange.getRequestURI().getPath());
		}
		return false;
	}
	
	private T findEndpointMBean(HttpExchange exchange) {
		String query = exchange.getRequestURI().getQuery();
		String request = exchange.getRequestURI().getPath() + (query == null ? "" : '?' + query);
		return registry.getMBean(request);
	}
	
	/**
	 * Handles the request when an MBean can be found for the endpoint.
	 *
	 * @param api      The API the request is against
	 * @param exchange The exchange containing the request
	 * @param mBean    The MBean associated with the request
	 * @return True if the response has been written, false otherwise
	 */
	protected abstract boolean handleRequest(Api api, HttpExchange exchange, T mBean);
}
