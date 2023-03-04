package com.team766.robot;

import com.team766.framework.Procedure;

import java.io.IOException;

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
	
	public OI() {
		loggerCategory = Category.OPERATOR_INTERFACE;

		joystick0 = RobotProvider.instance.getJoystick(0);
		joystick1 = RobotProvider.instance.getJoystick(1);
		joystick2 = RobotProvider.instance.getJoystick(2);
	}
	
	public void run(Context context) {
		int color = 0;
		context.takeOwnership(Robot.candle);
		Robot.candle.setColor(0, 0, 0);
		context.releaseOwnership(Robot.candle);
		
		while (true) {
			// wait for driver station data (and refresh it using the WPILib APIs)
			context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
			RobotProvider.instance.refreshDriverStationData();

			if (joystick0.getButtonPressed(1)) {
				color = (++color) % 3; 
				context.takeOwnership(Robot.candle);
				log(color + " ");
				switch (color) {
					case 0: Robot.candle.setColor(0, 0, 0); break;
					case 1: Robot.candle.setColor(255, 0, 255); break;
					case 2: Robot.candle.setColor(255, 255, 0); break;
				}
				context.releaseOwnership(Robot.candle);
			}

			// Add driver controls here - make sure to take/release ownership
			// of mechanisms when appropriate.
			
		}
	}
}
