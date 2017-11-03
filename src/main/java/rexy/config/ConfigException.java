package rexy.config;

import rexy.exception.RexyException;

/**
 * An exception that is thrown due to invalid configuration.
 */
public class ConfigException extends RexyException {
	
	public ConfigException(String message) {
		super(message);
	}
	
	public ConfigException(String message, Throwable cause) {
		super(message, cause);
	}
}