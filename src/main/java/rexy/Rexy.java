package rexy;

import rexy.config.Config;
import rexy.feature.Feature;
import rexy.http.Server;

import java.util.Collections;
import java.util.List;

public class Rexy {
	
	public static void main(String[] args) throws Exception {
		Config config = parseConfig();
		List<Feature> features = initFeatures(config);
		new Server(config, features).start();
	}
	
	private static Config parseConfig() {
		return null;
	}
	
	private static List<Feature> initFeatures(Config config) {
		return Collections.emptyList();
	}
}