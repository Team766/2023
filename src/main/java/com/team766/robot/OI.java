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
	double turningValue = 0;
	
	public OI() {
		loggerCategory = Category.OPERATOR_INTERFACE;

		joystick0 = RobotProvider.instance.getJoystick(0);
		joystick1 = RobotProvider.instance.getJoystick(1);
		joystick2 = RobotProvider.instance.getJoystick(2);
	}
	
	public void run(Context context) {
		double prev_time = RobotProvider.instance.getClock().getTime();
		context.takeOwnership(Robot.gyro);
		context.takeOwnership(Robot.drive);
		//Robot.gyro.resetGyro();
		Robot.drive.setFrontRightEncoders();
		Robot.drive.setFrontLeftEncoders();
		Robot.drive.setBackRightEncoders();
		Robot.drive.setBackLeftEncoders();

		while (true) {
			// Add driver controls here - make sure to take/release ownership
			// of mechanisms when appropriate.
			if(joystick1.getButton(2)){
				Robot.drive.setGyro(0);
			}else{
				Robot.drive.setGyro(Robot.gyro.getGyroYaw());
			}		
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
			//log(Robot.gyro.getGyroYaw());			
			//TODO: fix defense: the robot basically locks up if there is defense
			// if(joystick0.getButton(InputConstants.CROSS_DEFENSE)){
			// 	context.startAsync(new DefenseCross());
			// }
			
			/*if(Math.pow(Math.pow(joystick0.getAxis(InputConstants.AXIS_LEFT_RIGHT),2) + Math.pow(joystick0.getAxis(InputConstants.AXIS_FORWARD_BACKWARD),2), 0.5) > 0.15 ){
				Robot.drive.drive2D(
					((joystick0.getAxis(InputConstants.AXIS_LEFT_RIGHT))),
					((joystick0.getAxis(InputConstants.AXIS_FORWARD_BACKWARD)))
				);
			}  else {
				if(Math.abs(joystick0.getAxis(InputConstants.AXIS_TWIST))>=0.1){
					Robot.drive.turning(joystick0.getAxis(InputConstants.AXIS_TWIST));
				} else {
				Robot.drive.stopDriveMotors();
				Robot.drive.stopSteerMotors();
				}
			}*/
			if(joystick0.getButtonPressed(1))
				Robot.gyro.resetGyro();
			
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
			if(Math.abs(LeftJoystick_X)+
			Math.abs(LeftJoystick_Y)+  Math.abs(RightJoystick_X) > 0){
			Robot.drive.swerveDrive( 
				(LeftJoystick_X),
			 	(LeftJoystick_Y),
			 	(RightJoystick_X)
			);} else{
				Robot.drive.stopDriveMotors();
				Robot.drive.stopSteerMotors();				
			}

			if (joystick0.getButtonPressed(11)) {
				Robot.drive.resetCurrentPosition();
			}

			if (joystick1.getButtonPressed(11)) {
				context.takeOwnership(Robot.drive);
				new FollowPoints().run(context);
			}

			double cur_time = RobotProvider.instance.getClock().getTime();
				context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
		}
	}
}
