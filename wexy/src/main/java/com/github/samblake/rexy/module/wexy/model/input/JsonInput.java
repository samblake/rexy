package com.github.samblake.rexy.module.wexy.model.input;

import com.github.samblake.rexy.utils.Json;

public class JsonInput extends FormattedInput {
	
	public JsonInput(String label, String name, String value) {
		super("json", label, name, value);
	}
	
	@Override
	protected String prettyPrint(String value) {
		return Json.prettyPrint(value);
	}
	
}