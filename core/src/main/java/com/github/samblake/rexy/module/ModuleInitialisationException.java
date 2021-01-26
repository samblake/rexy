package com.github.samblake.rexy.module;

import com.github.samblake.rexy.exception.RexyException;

/**
 * An exception that is thrown when a module can not be initialised. If this is thrown the server will
 * fail to start.
 */
public class ModuleInitialisationException extends RexyException {
	
	public ModuleInitialisationException(RexyModule module) {
		this("Could not initialise " + module.getName());
	}
	
	public ModuleInitialisationException(String message) {
		super(message);
	}
	
	public ModuleInitialisationException(RexyModule module, Throwable cause) {
		super("Could not initialise " + module.getName(), cause);
	}
	
	public ModuleInitialisationException(RexyModule module, String message, Throwable cause) {
		super("Could not initialise " + module.getName() + ':' + message, cause);
	}
	
}