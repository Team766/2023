package com.team766.robot;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.procedures.*;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {
	private final static int LEFT_STICK_X_AXIS = 0;
	private final static int LEFT_STICK_Y_AXIS = 1;
	private final static int RIGHT_STICK_X_AXIS = 2;
	private final static int RIGHT_STICK_Y_AXIS = 3;

	private final JoystickReader gamepad;
	
	public OI() {
		loggerCategory = Category.OPERATOR_INTERFACE;

		gamepad = RobotProvider.instance.getJoystick(0);
	}
	
	public void run(Context context) {
		context.takeOwnership(Robot.drive);

		while (true) {
			// Add driver controls here - make sure to take/release ownership
			// of mechanisms when appropriate.
			double leftX = gamepad.getAxis(LEFT_STICK_X_AXIS);
			double leftY = gamepad.getAxis(LEFT_STICK_Y_AXIS);
			double rightX = gamepad.getAxis(RIGHT_STICK_X_AXIS);
			double rightY = gamepad.getAxis(RIGHT_STICK_Y_AXIS);

			log(/*"leftX: " + leftX + */ ", leftY: " + leftY + ", rightX: " + rightX /*+ ", rightY: " + rightY */);

			Robot.drive.setArcadeDrivePower(-leftY, rightX);
			context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
		}
	}
}
