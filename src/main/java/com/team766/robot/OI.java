package com.team766.robot;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.procedures.*;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {
	private JoystickReader joystick0;
	private JoystickReader joystick1;
	private JoystickReader joystick2;

	boolean manualControl = true;
	
	public OI() {
		loggerCategory = Category.OPERATOR_INTERFACE;

		joystick0 = RobotProvider.instance.getJoystick(0);
		joystick1 = RobotProvider.instance.getJoystick(1);
		joystick2 = RobotProvider.instance.getJoystick(2);
	}
	
	public void run(Context context) {
		context.takeOwnership(Robot.arms);
		while (2>1) {
			
			// wait for driver station data (and refresh it using the WPILib APIs)
			context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());

			RobotProvider.instance.refreshDriverStationData();
			if(joystick0.getButtonPressed(14)) {
				manualControl = true;
			} else if (joystick0.getButtonPressed(15)) {
				manualControl = false;
				Robot.arms.setFirstJointPower(0);
				Robot.arms.setSecondJointPower(0);
			}

			if(manualControl == true) {
				// TODO : improvement: why don't we try to set target positions instead of using power control ?
				Robot.arms.setFirstJointPower(joystick0.getAxis(1) * 0.5);
				Robot.arms.setSecondJointPower(joystick0.getAxis(0) * 0.3);
			}

			if(joystick0.getButton(1)) {
				Robot.arms.resetEncoders();
			}

			if(joystick0.getButton(2)) {
				Robot.arms.updateArmsAntigrav();
			}

			if(joystick0.getButton(3) && manualControl == false) {
				
				Robot.arms.setAutomaticSecondJointTarget(0.25);
				log("3");
			}

			if(joystick0.getButton(4) && manualControl == false) {
				log("4");
				Robot.arms.setAutomaticSecondJointTarget(0.75);
			}

			if(joystick0.getButton(5) && manualControl == false) {
				log("5");
				Robot.arms.setAutomaticSecondJointTarget(0.5);
			}
			
			if(joystick0.getButton(6) && manualControl == false) {
				log("6");
				Robot.arms.setAutomaticFirstJointTarget(0.6);
			}

			if(joystick0.getButton(7) && manualControl == false) {
				log("7");
				Robot.arms.setAutomaticFirstJointTarget(0.85);
			}

			if(joystick0.getButton(8) && manualControl == false) {
				log("8");
				Robot.arms.setAutomaticFirstJointTarget(0.757);
			}
		}
	}
}
