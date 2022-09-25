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
	private JoystickReader m_joystick0;
	private JoystickReader m_joystick1;
	private JoystickReader m_joystick2;
	
	public OI() {
		loggerCategory = Category.OPERATOR_INTERFACE;

		m_joystick0 = RobotProvider.instance.getJoystick(0);
		m_joystick1 = RobotProvider.instance.getJoystick(1);
		m_joystick2 = RobotProvider.instance.getJoystick(2);
	}
	
	public void run(Context context) {
		while (true) {
			// Add driver controls here - make sure to take/release ownership
			// of mechanisms when appropriate.
			

			context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
		}
	}
}
