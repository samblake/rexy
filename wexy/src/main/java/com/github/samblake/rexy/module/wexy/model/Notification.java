package com.github.samblake.rexy.module.wexy.model;

import static com.github.samblake.rexy.module.wexy.model.Notification.Type.FAILURE;
import static com.github.samblake.rexy.module.wexy.model.Notification.Type.SUCCESS;
import static java.lang.String.format;

public class Notification {
	
	private final Type type;
	private final String message;
	
	public Notification(Type type, String message) {
		this.type = type;
		this.message = message;
	}
	
	public String getType() {
		return type.name().toLowerCase();
	}
	
	public String getMessage() {
		return message;
	}
	
	public enum Type {
		SUCCESS, FAILURE;
	}
	
	public static Notification failure(String message, Object... values) {
		return new Notification(FAILURE, format(message, values));
	}
	
	public static Notification success(String message, Object... values) {
		return new Notification(SUCCESS, format(message, values));
	}
	
}