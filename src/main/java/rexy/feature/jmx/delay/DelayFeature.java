package rexy.feature.jmx.delay;

import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.model.Api;
import rexy.feature.jmx.JmxFeature;
import rexy.feature.jmx.JmxRegistry;

import static java.util.concurrent.TimeUnit.SECONDS;

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
