package com.team766.logging;

public class SerializationUtils {
	public static void valueToProto(Object object, LogValue.Builder value) {
		if (object instanceof Byte ||
				object instanceof Short ||
				object instanceof Integer ||
				object instanceof Long) {
			value.setIntValue(((Number)object).longValue());
		} else if (object instanceof Character) {
			value.setStringValue(((Character)object).toString());
		} else if (object instanceof Number) {
			// If object is a Number but not one of the integer types, treat it
			// as a double (this primarily handles Float and Double values, but
			// also handles any weird other type that might inherit from Number)
			value.setFloatValue(((Number)object).doubleValue());
		} else if (object instanceof Boolean) {
			value.setBoolValue(((Boolean)object).booleanValue());
		} else if (object instanceof String) {
			value.setStringValue((String)object);
		} else if (object == null) {
			value.clearKind();
		} else if (object instanceof Loggable) {
			((Loggable)object).toLogValue(value);
		} else {
			throw new IllegalArgumentException(
				"Value of type " + object.getClass().getName() + " isn't loggable");
		}
	}

	public static Object protoToValue(LogValue value) {
		switch (value.getKindCase()) {
			case KIND_NOT_SET:
				return null;
			case BOOL_VALUE:
				return value.getBoolValue();
			case FLOAT_VALUE:
				return value.getFloatValue();
			case INT_VALUE:
				return value.getIntValue();
			case LIST:
				return value.getList().getElementList().stream()
					.map(SerializationUtils::protoToValue).toArray();
			case STRING_VALUE:
				return value.getStringValue();
		}
		throw new IllegalArgumentException(
			"Unsupported LogValue kind: " + value.getKindCase());
	}
}