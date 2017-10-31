package rexy.feature;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.ImplementingClassMatchProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class FeatureScanner {
	private static final Logger logger = LoggerFactory.getLogger(FeatureScanner.class);
	
	private final List<String> scanPackages;
	
	public FeatureScanner(List<String> scanPackages) {
		this.scanPackages = scanPackages;
	}
	
	public List<Feature> scan() {
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
