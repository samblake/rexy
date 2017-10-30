package rexy.feature.jmx;

import com.sun.net.httpserver.HttpExchange;
import rexy.config.Api;
import rexy.feature.FeatureAdapter;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class JmxFeature extends FeatureAdapter {
	
	@Override
	public boolean onRequest(Api api, HttpExchange exchange) {
		/*MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = new ObjectName("com.example:type=Hello");*/
		
		return super.onRequest(api, exchange);
	}
}
