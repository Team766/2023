package com.team766.framework;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;

public class AutonomousProcedureUtils {
	public static Procedure getProcedure(Enum<?> autonomousModesEnum) {
		if (autonomousModesEnum == null) {
			Logger.get(Category.AUTONOMOUS).logRaw(Severity.WARNING, "No autonomous mode selected");
			return Procedure.NO_OP;
		}
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
		} catch (Exception ex) {
			Logger.get(Category.AUTONOMOUS).logRaw(Severity.ERROR, "Exception while selecting autonomous mode: " + ex);
			ex.printStackTrace();
			LoggerExceptionUtils.logException(ex);
			return Procedure.NO_OP;
		}
	}
}

class InvalidAutonomousProcedure extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidAutonomousProcedure(String message) {
		super(message);
	}
}
