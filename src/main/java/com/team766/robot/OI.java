package com.team766.robot;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.procedures.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {
	private JoystickReader controller;
	private double Controller_X = 0;
	private double Controller_Y = 0;
	private double Controller_Z = 0;
	private double Controller_Theta = 0;

	private boolean isCross = false;
	double turningValue = 0;
	
	public OI() {
		loggerCategory = Category.OPERATOR_INTERFACE;

		controller = RobotProvider.instance.getJoystick(0);
	}
	
	public void run(Context context) {
		double prev_time = RobotProvider.instance.getClock().getTime();
		context.takeOwnership(Robot.gyro);
		context.takeOwnership(Robot.drive);
		Robot.gyro.resetGyro();
		Robot.drive.setFrontRightEncoders();
		Robot.drive.setFrontLeftEncoders();
		Robot.drive.setBackRightEncoders();
		Robot.drive.setBackLeftEncoders();

		while (true) {
			Robot.drive.smrtStuff();
			// wait for driver station data (and refresh it using the WPILib APIs)
			context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
			RobotProvider.instance.refreshDriverStationData();

			// Add driver controls here - make sure to take/release ownership
			// of mechanisms when appropriate.
			/*if(joystick1.getButton(2)){
				Robot.drive.setGyro(0);
			} else {
				Robot.drive.setGyro(Robot.gyro.getGyroYaw());
			}*/
			if(Math.abs(joystick1.getAxis(InputConstants.AXIS_FORWARD_BACKWARD)) > 0.05){
				RightJoystick_Y = joystick1.getAxis(InputConstants.AXIS_FORWARD_BACKWARD);
			} else {
				RightJoystick_Y = 0;
			}
			if(Math.abs(joystick1.getAxis(InputConstants.AXIS_LEFT_RIGHT)) > 0.05){
				RightJoystick_X = joystick1.getAxis(InputConstants.AXIS_LEFT_RIGHT)/2;
				if(joystick1.getButton(3)){
					RightJoystick_X *= 2;
				}	
			} else {
				RightJoystick_X = 0;	
			}
			if(Math.abs(joystick1.getAxis(InputConstants.AXIS_TWIST)) > 0.05){
				RightJoystick_Theta = joystick1.getAxis(InputConstants.AXIS_TWIST);
			} else {
				RightJoystick_Theta = 0;
			}
			if(Math.abs(joystick0.getAxis(InputConstants.AXIS_FORWARD_BACKWARD)) > 0.05){
				LeftJoystick_Y = joystick0.getAxis(InputConstants.AXIS_FORWARD_BACKWARD);
			} else {
				LeftJoystick_Y = 0;
			}
			if(Math.abs(joystick0.getAxis(InputConstants.AXIS_LEFT_RIGHT)) > 0.05){
				LeftJoystick_X = joystick0.getAxis(InputConstants.AXIS_LEFT_RIGHT);
			} else {
				LeftJoystick_X = 0;
			}
			if(Math.abs(joystick0.getAxis(InputConstants.AXIS_TWIST)) > 0.05){
				LeftJoystick_Theta = joystick0.getAxis(InputConstants.AXIS_TWIST);
			} else {
				LeftJoystick_Theta = 0;
			}
			
			if(joystick0.getButtonPressed(1)) {
				log("Resetting Gyro");
				Robot.gyro.resetGyro();
			}

			if(joystick0.getButtonPressed(11))
				Robot.drive.resetCurrentPosition();

			if(joystick1.getButtonPressed(1))
				isCross = !isCross;
			
			
			if(joystick0.getButtonPressed(2)){
				Robot.drive.setFrontRightEncoders();
				Robot.drive.setFrontLeftEncoders();
				Robot.drive.setBackRightEncoders();
				Robot.drive.setBackLeftEncoders();
			}
			// if(joystick1.getButton(1)){
			// 	turningValue = joystick1.getAxis(InputConstants.AXIS_LEFT_RIGHT); 
			// } else {
			// 	turningValue = 0;
			// }

			SmartDashboard.putNumber("Gyro Value", Robot.gyro.getGyroYaw());
			
			if (isCross)  {
				context.startAsync(new setCross());
			} else if(Math.abs(LeftJoystick_X)+
			Math.abs(LeftJoystick_Y) +  Math.abs(RightJoystick_X) > 0) {
				Robot.drive.swerveDrive( 
					(LeftJoystick_X),
			 		(LeftJoystick_Y),
			 		(RightJoystick_X));
			} else {
				Robot.drive.stopDriveMotors();
				Robot.drive.stopSteerMotors();				
			} 
		}
	}
}
