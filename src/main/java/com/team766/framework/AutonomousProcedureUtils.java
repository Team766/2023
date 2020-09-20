package com.team766.framework;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class AutonomousProcedureUtils {
	public static Procedure getProcedure(Enum<?> autonomousModesEnum) {
		Field f;
		try {
			f = autonomousModesEnum.getClass().getField(autonomousModesEnum.name());
			AutonomousProcedure annotation = f.getAnnotation(AutonomousProcedure.class);
			try {
				return annotation.procedureClass().getConstructor().newInstance();
			} catch (NoSuchMethodException cause) {
				throw new InvalidAutonomousProcedure(annotation.procedureClass().getName()
						+ " cannot be used as an autonomous mode because it doesn't have a constructor that takes 0 parameters");
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
				throw new RuntimeException(ex);
			}
		} catch (NoSuchFieldException | SecurityException ex) {
			throw new RuntimeException(ex);
		}
	}
}

class InvalidAutonomousProcedure extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidAutonomousProcedure(String message) {
		super(message);
	}
}
