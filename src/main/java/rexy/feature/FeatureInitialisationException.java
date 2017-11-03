package rexy.feature;

import rexy.exception.RexyException;

/**
 * An exception that is thrown when a feature could not be initialised. If this is thrown the server will
 * fail to start .
 */
public class FeatureInitialisationException extends RexyException {
	
	public FeatureInitialisationException(Feature feature) {
		this("Could not initialise " + feature.getName());
	}
	
	public FeatureInitialisationException(String message) {
		super(message);
	}
	
	public FeatureInitialisationException(Feature feature, Throwable cause) {
		super("Could not initialise " + feature.getName(), cause);
	}
	
	public FeatureInitialisationException(Feature feature, String message, Throwable cause) {
		super("Could not initialise " + feature.getName() + ':' + message, cause);
	}
}
