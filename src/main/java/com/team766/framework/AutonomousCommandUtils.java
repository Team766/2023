package com.team766.framework;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class AutonomousCommandUtils {
	public static Command getCommand(Enum<?> autonomousModesEnum) {
		Field f;
		try {
			f = autonomousModesEnum.getClass().getField(autonomousModesEnum.name());
			AutonomousCommand annotation = f.getAnnotation(AutonomousCommand.class);
			try {
				return annotation.commandClass().getConstructor().newInstance();
			} catch (NoSuchMethodException cause) {
				throw new InvalidAutonomousCommand(annotation.commandClass().getName()
						+ " cannot be used as an autonomous mode because it doesn't have a constructor that takes 0 parameters");
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
				throw new RuntimeException(ex);
			}
		} catch (NoSuchFieldException | SecurityException ex) {
			throw new RuntimeException(ex);
		}
	}
}

class InvalidAutonomousCommand extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidAutonomousCommand(String message) {
		super(message);
	}
}
