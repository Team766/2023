package com.team766.frc2018;

import com.team766.framework.Command;
import com.team766.frc2018.commands.ExampleSequence;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI extends Command {
	private JoystickReader m_joystick1;
	private JoystickReader m_joystick2;
	
	public OI() {
		m_joystick1 = RobotProvider.instance.getJoystick(1);
		m_joystick2 = RobotProvider.instance.getJoystick(2);
	}
	
	public void run() {
		double leftPower = m_joystick1.getRawAxis(2);
		double rightPower = m_joystick2.getRawAxis(2);
		Robot.drive.setDrivePower(leftPower, rightPower);
		
		if (m_joystick2.getTriggerPressed()) {
			new ExampleSequence().start();
		}
	}
}
