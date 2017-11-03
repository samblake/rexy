package rexy.feature.jmx;

import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rexy.config.model.Api;
import rexy.config.model.Endpoint;
import rexy.feature.FeatureAdapter;
import rexy.feature.FeatureInitialisationException;

import javax.management.JMException;
import java.util.Map;

public abstract class JmxFeature<T> extends FeatureAdapter {
	private static final Logger logger = LoggerFactory.getLogger(JmxFeature.class);
	
	private JmxRegistry<T> registry;
	
	@Override
	public void init(Map<String, Object> jsonNode) throws FeatureInitialisationException {
		registry = getRegistry();
	}
	
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
		T endpoint = findEndpoint(exchange);
		
		if (endpoint != null) {
			if (handleRequest(api, exchange, endpoint)) {
				return true;
			}
		}
		else {
			logger.warn("No match for " + exchange.getRequestURI().getPath());
		}
		return false;
	}
	
	private T findEndpoint(HttpExchange exchange) {
		String query = exchange.getRequestURI().getQuery();
		String request = exchange.getRequestURI().getPath() + (query == null ? "" : '?' + query);
		return registry.getEndpoint(request);
	}
	
	protected abstract boolean handleRequest(Api api, HttpExchange exchange, T endpoint);
}
