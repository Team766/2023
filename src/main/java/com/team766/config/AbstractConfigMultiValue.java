package com.team766.config;

abstract class AbstractConfigMultiValue<E> extends AbstractConfigValue<E[]> {
	protected AbstractConfigMultiValue(String key) {
		super(key);
	}

	protected String[] splitConfigString(String configString) {
		return configString.split(",");
	}
}