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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
			Config config = parseConfig();
			List<Feature> features = initFeatures(config, findFeatures(config.getScanPackages()));
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
		
		List<Feature> enabledFeatures = new LinkedList<>();
		for (Feature feature : features) {
			String featureName = feature.getName();
			logger.debug("Starting feature: " + featureName);
			rexy.config.Feature featureConfig = config.getFeatures().get(featureName);
			if ((featureConfig != null && featureConfig.isEnabled()) || feature.enabledDefault()) {
				feature.init(featureConfig == null ? Collections.<String, Object>emptyMap() : featureConfig.getConfig());
				enabledFeatures.add(feature);
				logger.info("Started feature: " + featureName);
			}
		}
		
		return enabledFeatures;
	}
	
	private List<Feature> findFeatures(List<String> scanPackages) {
		List<Feature> features = new ArrayList<>();
		scan(features, getClass().getPackage().getName());
		for (String scanPackage : scanPackages) {
			scan(features, scanPackage);
		}
		return features;
	}
	
	private void scan(List<Feature> features, String scanPackage) {
		new FastClasspathScanner(scanPackage)
				.matchClassesImplementing(Feature.class, new FeatureCreator(features))
				.scan();
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