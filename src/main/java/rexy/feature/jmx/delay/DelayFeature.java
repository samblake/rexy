package rexy.feature.jmx.delay;

import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.model.Api;
import rexy.feature.jmx.JmxFeature;
import rexy.feature.jmx.JmxRegistry;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * <p>A feature that intercepts a request and optionally adds a delay before the next feature in the chain.</p>
 *
 * <p>To configure a delay an API is created in the configuration. Each endpoint has an an MBean associated with
 * it under the name {@code Rexy/[API name]/[Endpoint name]/delay}. The MBean has a single property - delay. If
 * the value of <i>delay</i> is greater then zero then a delay of that number of seconds will be artificially
 * introduced.</p>
 *
 * <p>For example, if the following configuration an MBean would be configured under
 * {@code Rexy/metaweather/location/delay}.</p>
 *
 * <p>{@code
 * "apis": [
 * {
 * "name": "metaweather",
 * ...
 * "endpoints": [
 * {
 * "name": "location",
 * ...
 * }
 * }
 * ]
 * }</p>
 */
public class DelayFeature extends JmxFeature<DelayEndpoint> {
	private static final Logger logger = LogManager.getLogger(DelayFeature.class);
	
	@Override
	protected JmxRegistry<DelayEndpoint> getRegistry() {
		return DelayRegistry.getInstance();
	}
	
	@Override
	protected boolean handleRequest(Api api, HttpExchange exchange, DelayEndpoint mBean) {
		if (mBean.getDelay() > 0) {
			String requestPath = exchange.getRequestURI().getPath();
			logger.info("Delaying request for " + requestPath + " by " + mBean.getDelay() + " seconds");
			
			try {
				Thread.sleep(SECONDS.toMillis(mBean.getDelay()));
			}
			catch (InterruptedException e) {
				logger.error("Error delaying response for " + requestPath, e);
			}
		}
		return false;
	}
}
