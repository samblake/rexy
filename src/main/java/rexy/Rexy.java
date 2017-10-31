package rexy;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.ImplementingClassMatchProcessor;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rexy.config.Config;
import rexy.config.ConfigException;
import rexy.exception.RexyException;
import rexy.feature.Feature;
import rexy.feature.FeatureInitialisationException;
import rexy.http.Server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class Rexy {
	private static final Logger logger = LoggerFactory.getLogger(Rexy.class);
	
	private static final String DEFAULT_PATH = "rexy.json";
	
	private final String configPath;
	
	public Rexy(String configPath) {
		this.configPath = configPath;
	}
	
	public static void main(String[] args) {
		String path = (args.length == 0) ? DEFAULT_PATH : args[0];
		new Rexy(path).start();
	}
	
	public void start() {
		logger.debug("Starting Rexy");
		try {
			Config config = parseConfig();
			List<Feature> features = initFeatures(config, findFeatures());
			new Server(config, features).start();
			logger.info("Rexy started on port " + config.getPort());
		}
		catch (IOException | RexyException e) {
			logger.error("Rexy server failed to start", e);
		}
	}
	
	private Config parseConfig() throws ConfigException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			InputStream inputStream = getClass().getResourceAsStream('/' + configPath);
			if (inputStream != null) {
				return mapper.readValue(inputStream, Config.class);
			}
			
			logger.debug("Config not found on classpath, looking for absolute file");
			return mapper.readValue(new File(configPath), Config.class);
		}
		catch (IOException e) {
			throw new ConfigException("Could not read " + configPath, e);
		}
	}
	
	private List<Feature> initFeatures(Config config, List<Feature> features)
			throws ConfigException, FeatureInitialisationException {
		
		List<Feature> enabledFeatures = new ArrayList<>(config.getFeatures().size());
		for (String featureName : config.getFeatures()) {
			logger.debug("Starting feature: " + featureName);
			Feature feature = findFeature(features, featureName);
			feature.init(null); // TODO from config
			enabledFeatures.add(feature);
			logger.info("Started feature: " + featureName);
		}
		
		return enabledFeatures;
	}
	
	private List<Feature> findFeatures() {
		List<Feature> features = new ArrayList<>();
		new FastClasspathScanner(
				getClass().getPackage().getName()) // TODO allow configurable packages
				.matchClassesImplementing(Feature.class, new FeatureCreator(features))
				.scan();
		return features;
	}
	
	private Feature findFeature(List<Feature> features, String featureName) throws ConfigException {
		for (Feature feature : features) {
			if (feature.getName().equals(featureName)) {
				return feature;
			}
		}
		throw new ConfigException("Could not find feature " + featureName);
	}
	
	private static class FeatureCreator implements ImplementingClassMatchProcessor<Feature> {
		private final List<Feature> features;
		
		FeatureCreator(List<Feature> features) {this.features = features;}
		
		@Override
		public void processMatch(Class<? extends Feature> featureClass) {
			if (!Modifier.isAbstract(featureClass.getModifiers()) && !Modifier
					.isInterface(featureClass.getModifiers())) {
				logger.debug("Found feature: " + featureClass.getCanonicalName());
				try {
					Constructor<? extends Feature> constructor = featureClass.getConstructor();
					constructor.setAccessible(true);
					features.add(constructor.newInstance());
				}
				catch (ReflectiveOperationException e) {
					logger.error("Could not create " + featureClass.getCanonicalName(), e);
				}
			}
		}
	}
}