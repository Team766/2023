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
		context.startAsync(new DisplayImage("cone.png", true));
		int imageDisplayed = 0;

		String[] imageList = {"cone.png", "cone-2.png", "cube.png", "cube-2.png", "progamer.png"};
		
		while (true) {
			// wait for driver station data (and refresh it using the WPILib APIs)
			context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
			RobotProvider.instance.refreshDriverStationData();

			// Add driver controls here - make sure to take/release ownership
			// of mechanisms when appropriate.
			if (joystick0.getButtonPressed(1)) {
				imageDisplayed = (++imageDisplayed) % imageList.length;

				context.startAsync(new DisplayImage(imageList[imageDisplayed], true));
			}
		}
	}
}
