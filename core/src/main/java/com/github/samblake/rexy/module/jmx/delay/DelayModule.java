package com.github.samblake.rexy.module.jmx.delay;

import com.codepoetics.ambivalence.Either;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.samblake.rexy.config.model.Api;
import com.github.samblake.rexy.http.request.RexyRequest;
import com.github.samblake.rexy.http.response.RexyResponse;
import com.github.samblake.rexy.module.jmx.JmxModule;
import com.github.samblake.rexy.module.jmx.JmxRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.codepoetics.ambivalence.Either.ofLeft;
import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * <p>A module that intercepts a request and optionally adds a delay before the next module in the chain.</p>
 *
 * <p>To configure a delay an API is created in the configuration. Each endpoint has an an MBean associated with
 * it under the name {@code Rexy/[API name]/[Endpoint name]/delay}. The MBean has a single property - delay. If
 * the value of <i>delay</i> is greater then zero then a delay of that number of seconds will be introduced.</p>
 *
 * <p>For example, in the following configuration an MBean would be registered under
 * {@code Rexy/metaweather/location/delay}.</p>
 *
 * <pre><code>{@code
 * "apis": [
 *   {
 *     "name": "metaweather",
 *     ...
 *     "endpoints": [
 *     {
 *       "name": "location",
 *       ...
 *     }
 *   }
 * ]
 * }</code></pre>
 */
public class DelayModule extends JmxModule<DelayEndpoint> {
	private static final Logger logger = LogManager.getLogger(DelayModule.class);
	
	@Override
	protected JmxRegistry<DelayEndpoint> createRegistry(JsonNode config) {
		return new DelayRegistry();
	}
	
	@Override
	protected Either<RexyRequest, RexyResponse> handleRequest(Api api, RexyRequest request, DelayEndpoint mBean) {
		if (mBean.getDelay() > 0) {
			String requestPath = request.getUri();
			logger.info("Delaying request for " + requestPath + " by " + mBean.getDelay() + " seconds");
			
			try {
				sleep(SECONDS.toMillis(mBean.getDelay()));
			}
			catch (InterruptedException e) {
				logger.error("Error delaying response for " + requestPath, e);
			}
		}
		
		return ofLeft(request);
	}
	
}