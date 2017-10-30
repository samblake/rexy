package rexy.config;

import rexy.exception.RexyException;

public class ConfigException extends RexyException {

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}