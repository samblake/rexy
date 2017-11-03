package rexy.feature.jmx.delay;

import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rexy.config.model.Api;
import rexy.feature.jmx.JmxFeature;
import rexy.feature.jmx.JmxRegistry;

import java.util.concurrent.TimeUnit;

public class DelayFeature extends JmxFeature<DelayEndpoint> {
	private static final Logger logger = LoggerFactory.getLogger(DelayFeature.class);
	
	@Override
	protected JmxRegistry<DelayEndpoint> getRegistry() {
		return DelayRegistry.getInstance();
	}
	
	@Override
	protected boolean handleRequest(Api api, HttpExchange exchange, DelayEndpoint endpoint) {
		if (endpoint.getDelay() > 0) {
			logger.info("Delaying response for " + exchange.getRequestURI().getPath());
			
			try {
				Thread.sleep(TimeUnit.SECONDS.toMillis(endpoint.getDelay()));
			}
			catch (InterruptedException e) {
				logger.error("Error sending response for " + exchange.getRequestURI().getPath(), e);
			}
		}
		return false;
	}
}
