package rexy.feature;

import rexy.exception.RexyException;

public class FeatureInitialisationException extends RexyException {
	
	public FeatureInitialisationException(Feature feature) {
		super("Could not initialise " + feature.getName());
	}
	
	public FeatureInitialisationException(Feature feature, Throwable cause) {
		super("Could not initialise " + feature.getName(), cause);
	}
	
	public FeatureInitialisationException(Feature feature, String message, Throwable cause) {
		super("Could not initialise " + feature.getName() + ":" + message, cause);
	}
}
