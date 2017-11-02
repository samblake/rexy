package rexy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rexy.config.ConfigParser;
import rexy.config.model.Config;
import rexy.exception.RexyException;
import rexy.feature.Feature;
import rexy.feature.FeatureInitialisationException;
import rexy.feature.FeatureScanner;
import rexy.http.Server;

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
	private static final Logger logger = LoggerFactory.getLogger(Rexy.class);
	
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
		logger.debug("Starting Rexy");
		try {
			Config config = new ConfigParser(configPath).parse();
			List<Feature> allFeatures = new FeatureScanner(config.getScanPackages()).scan();
			List<Feature> enabledFeatures = initFeatures(config, allFeatures);
			new Server(config, enabledFeatures).start();
			logger.info("Rexy started on port " + config.getPort());
		}
		catch (IOException | RexyException e) {
			logger.error("Rexy server failed to start", e);
		}
	}
	
	private List<Feature> initFeatures(Config config, List<Feature> features) throws FeatureInitialisationException {
		List<Feature> enabledFeatures = new LinkedList<>();
		Set<Entry<String, rexy.config.model.Feature>> featureConfigs = config.getFeatures().entrySet();
		for (Entry<String, rexy.config.model.Feature> featureConfig : featureConfigs) {
			initFeature(features, enabledFeatures, featureConfig);
		}
		
		return enabledFeatures;
	}
	
	private void initFeature(List<Feature> features, List<Feature> enabledFeatures, Entry<String, rexy.config.model.Feature> featureConfig)
			throws FeatureInitialisationException {
		String featureName = featureConfig.getKey();
		logger.debug("Starting feature: " + featureName);
		for (Feature feature : features) {
			if (feature.getName().equals(featureName)) {
				feature.init(featureConfig.getValue().getConfig());
			}
			enabledFeatures.add(feature);
			logger.info("Started feature: " + featureName);
		}
		throw new FeatureInitialisationException("Could not find feature " + featureName);
	}
}