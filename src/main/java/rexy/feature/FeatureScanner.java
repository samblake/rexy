package rexy.feature;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.ImplementingClassMatchProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Scans certain packages on the classpath to find features. The {@code rexy} classpath will always be
 * scanned. Additional packages may be supplied.
 */
public class FeatureScanner {
	private static final Logger logger = LoggerFactory.getLogger(FeatureScanner.class);
	
	private final List<String> scanPackages;
	
	/**
	 * Creates a scanner that will scan the {@code rexy} classpath as well as any additional
	 * classpaths supplied.
	 */
	public FeatureScanner(List<String> scanPackages) {
		this.scanPackages = scanPackages;
	}
	
	/**
	 * Scans the classspath for any concrete classes that implement {@link Feature features}.
	 *
	 * @return The features found with their no-args constructors called.
	 */
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
	
	/**
	 * Processes a {@link Feature feature} class. Checks to see if it's a concrete class and, if it is,
	 * call it's no-args constructor and adds it to the list of features.
	 */
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
