package rexy.feature;

public class FeatureInitialisationException extends Exception {
	
	public FeatureInitialisationException(Feature feature) {
		super("Could not initialise " + feature.getName());
	}
	
	public FeatureInitialisationException(Feature feature, Throwable cause) {
		super("Could not initialise " + feature.getName(), cause);
	}
}
