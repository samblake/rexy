package rexy.module;

import rexy.exception.RexyException;

/**
 * An exception that is thrown when a module can not be initialised. If this is thrown the server will
 * fail to start.
 */
public class ModuleInitialisationException extends RexyException {
	
	public ModuleInitialisationException(Module module) {
		this("Could not initialise " + module.getName());
	}
	
	public ModuleInitialisationException(String message) {
		super(message);
	}
	
	public ModuleInitialisationException(Module module, Throwable cause) {
		super("Could not initialise " + module.getName(), cause);
	}
	
	public ModuleInitialisationException(Module module, String message, Throwable cause) {
		super("Could not initialise " + module.getName() + ':' + message, cause);
	}
	
}