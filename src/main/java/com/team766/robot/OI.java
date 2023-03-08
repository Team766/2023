package com.team766.robot;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.logging.Severity;
import com.team766.robot.procedures.*;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {

	private JoystickReader joystick0;

	private boolean manualArmControl = false;

	public OI() {
		loggerCategory = Category.OPERATOR_INTERFACE;

		joystick0 = RobotProvider.instance.getJoystick(0);
	}
	
	public void run(Context context) {
		while (true) {
			// wait for driver station data (and refresh it using the WPILib APIs)
			context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
			RobotProvider.instance.refreshDriverStationData();

			// switch between states depending on button press
			if(joystick0.getButtonPressed(5)) {
				manualArmControl = false;
				context.startAsync(new ArmAutomatedControl());
			} else if(joystick0.getButtonPressed(6)) {
				manualArmControl = true;
				context.takeOwnership(Robot.arms);
			}

			if (manualArmControl) {
				double axisValue = joystick0.getAxis(1);

				// scale degrees per (rough) iteration from -1 thru 1 to ...
				double angularIncrement = axisValue * 10.0d;
				
				double motorAngle = Robot.arms.firstJoint.getMotorPosition();
				Robot.arms.firstJoint.setMotorPosition(motorAngle + angularIncrement);
			}

			// zero the arm
			if(joystick0.getButtonPressed(7)) {
				log(Severity.INFO, "Arm encoders reset");
				context.takeOwnership(Robot.arms);
				Robot.arms.resetEncoders();
			}

		}
	}
}
