package com.github.samblake.rexy.module.wexy.model.input;

import java.math.BigDecimal;

public class NumberInput extends ValueInput<BigDecimal> {
	
	public NumberInput(String label, String name, BigDecimal value) {
		super("number", label, name, value);
	}
	
}