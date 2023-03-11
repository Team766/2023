package com.team766.robot;

import com.team766.framework.Procedure;

import java.io.IOException;

import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.constants.InputConstants;
import com.team766.robot.procedures.*;
import com.team766.simulator.interfaces.ElectricalDevice.Input;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {
	private JoystickReader joystick0;
	private JoystickReader joystick1;
	private JoystickReader joystick2;
	private double RightJoystick_X = 0;
	private double RightJoystick_Y = 0;
	private double RightJoystick_Z = 0;
	private double RightJoystick_Theta = 0;
	private double LeftJoystick_X = 0;
	private double LeftJoystick_Y = 0;
	private double LeftJoystick_Z = 0;
	private double LeftJoystick_Theta = 0;
	private boolean isCross = false;
	double turningValue = 0;
	
	public OI() {
		loggerCategory = Category.OPERATOR_INTERFACE;

		joystick0 = RobotProvider.instance.getJoystick(0);
		joystick1 = RobotProvider.instance.getJoystick(1);
		joystick2 = RobotProvider.instance.getJoystick(2);
	}
	
	public void run(Context context) {
		while (true) {
			// wait for driver station data (and refresh it using the WPILib APIs)
			context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
			RobotProvider.instance.refreshDriverStationData();
			LeftJoystick_X = Robot.drive.correctedJoysticks(joystick0.getAxis(0));
			LeftJoystick_Y = Robot.drive.correctedJoysticks(joystick0.getAxis(1));
			RightJoystick_X = Robot.drive.correctedJoysticks(joystick1.getAxis(0));;

			// Add driver controls here - make sure to take/release ownership
			// of mechanisms when appropriate.
			if(joystick1.getButton(2)){
				Robot.drive.setGyro(0);
			}else{
				Robot.drive.setGyro(Robot.gyro.getGyroYaw());
			}		
			
		
			
			if(joystick0.getButtonPressed(1))
				Robot.gyro.resetGyro();

		}
	}
}
