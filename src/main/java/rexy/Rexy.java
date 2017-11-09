package rexy;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.ConfigParser;
import rexy.config.model.Config;
import rexy.exception.RexyException;
import rexy.feature.Feature;
import rexy.feature.FeatureInitialisationException;
import rexy.feature.FeatureScanner;
import rexy.http.RexyServer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A lightweight REST mock/proxy framework.
 *
 * <p>This is the main class. Run it from the command line with the path to the config file as the argument.
 * The path can either be an absolute file or relative to the classpath.</p>
 */
public class Rexy {
	private static final Logger logger = LogManager.getLogger(Rexy.class);
	
	private static final String DEFAULT_PATH = "rexy.json";
	
	private final String configPath;
	
	public Rexy(String configPath) {
		this.configPath = configPath;
	}
	
	/**
	 * Runs the Rexy server.
	 *
	 * @param args There should be a single argument, the path to the config file
	 */
	public static void main(String[] args) {
		String path = (args.length == 0) ? DEFAULT_PATH : args[0];
		new Rexy(path).start();
	}
	
	/**
	 * Reads the config file and runs the server.
	 */
	public void start() {
		logger.info("Starting Rexy");
		try {
			Config config = new ConfigParser(configPath).parse();
			List<Feature> allFeatures = new FeatureScanner(config.getScanPackages()).scan();
			List<Feature> enabledFeatures = initFeatures(config, allFeatures);
			new RexyServer(config, enabledFeatures).start();
			logger.info("Rexy started on port " + config.getPort());
		}
		catch (IOException | RexyException e) {
			logger.error("Rexy server failed to start", e);
		}
	}
	
	private List<Feature> initFeatures(Config config, List<Feature> features) throws FeatureInitialisationException {
		List<Feature> enabledFeatures = new LinkedList<>();
		Set<Entry<String, JsonNode>> featureConfigs = config.getFeatures().entrySet();
		for (Entry<String, JsonNode> featureConfig : featureConfigs) {
			initFeature(features, enabledFeatures, featureConfig);
		}
		
		return enabledFeatures;
	}
	
	private void initFeature(List<Feature> features, List<Feature> enabledFeatures, Entry<String, JsonNode> featureConfig)
			throws FeatureInitialisationException {
		String featureName = featureConfig.getKey();
		for (Feature feature : features) {
			if (feature.getName().equalsIgnoreCase(featureName)) {
				JsonNode enabled = featureConfig.getValue().get("enabled");
				if (enabled != null && enabled.isBoolean() && enabled.booleanValue()) {
					feature.init(featureConfig.getValue());
					enabledFeatures.add(feature);
					logger.info("Started feature: " + featureName);
				}
				else {
					logger.info("Feature disabled: " + featureName);
				}
				return;
			}
		}
		throw new FeatureInitialisationException("Could not find feature " + featureName);
	}
}