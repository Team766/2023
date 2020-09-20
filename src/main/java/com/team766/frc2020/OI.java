package com.team766.frc2020;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.frc2020.Robot;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {
	private JoystickReader m_joystick1;
    private JoystickReader m_joystick2;
    private JoystickReader m_joystick3;
	
	public OI() {
		m_joystick1 = RobotProvider.instance.getJoystick(1);
        m_joystick2 = RobotProvider.instance.getJoystick(2);
        m_joystick2 = RobotProvider.instance.getJoystick(3);
	}
	
	public void run(Context context) {

	}
}
