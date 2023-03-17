package com.team766.robot;

import com.team766.framework.Procedure;

import java.io.IOException;

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
	
	enum IntakeState {
		IDLE,
		SPINNINGOUT,
		SPINNINGIN
	}
	IntakeState intakeState = IntakeState.IDLE;

	public OI() {
		loggerCategory = Category.OPERATOR_INTERFACE;

		joystick0 = RobotProvider.instance.getJoystick(0);
		joystick1 = RobotProvider.instance.getJoystick(1);
		joystick2 = RobotProvider.instance.getJoystick(2);

	}
	
	public void run(Context context) {
		context.takeOwnership(Robot.drive);
		context.takeOwnership(Robot.intake);
		context.takeOwnership(Robot.arms);
		context.takeOwnership(Robot.grabber);
		context.takeOwnership(Robot.storage);
		context.takeOwnership(Robot.gyro);
		
		while (true) {
			// wait for driver station data (and refresh it using the WPILib APIs)
			context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
			RobotProvider.instance.refreshDriverStationData();
			LeftJoystick_X = Robot.drive.correctedJoysticks(joystick0.getAxis(0));
			LeftJoystick_Y = Robot.drive.correctedJoysticks(joystick0.getAxis(1));
			RightJoystick_X = Robot.drive.correctedJoysticks(joystick1.getAxis(0));;

			
			// Add driver controls here - make sure to take/release ownership
			// of mechanisms when appropriate.
			/* if (joystick0.getButtonPressed(15)){
				if (intakeState == IntakeState.IDLE){
					Robot.intake.intakeIn();
					Robot.storage.beltIn();
					intakeState = IntakeState.SPINNINGIN;
				} else {
					Robot.intake.intakeIdle();
					Robot.storage.beltIdle();
					intakeState = IntakeState.IDLE;
				}
			}

			if (joystick0.getButtonPressed(16)){
				if (intakeState == IntakeState.IDLE){
					Robot.intake.intakeOut();
					Robot.storage.beltOut();
					intakeState = IntakeState.SPINNINGOUT;
				} else {
					Robot.intake.intakeIdle();
					Robot.storage.beltIdle();
					intakeState = IntakeState.IDLE;
				}
			} */

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

			SmartDashboard.putNumber("Front left", Robot.drive.getFrontLeft());
			SmartDashboard.putNumber("Front right", Robot.drive.getFrontRight());
			SmartDashboard.putNumber("Back left", Robot.drive.getBackLeft());
			SmartDashboard.putNumber("Back right", Robot.drive.getBackRight());

			if (isCross)  {
				context.startAsync(new setCross());
			/*} else if (joystick0.getButton(3)) {
				Robot.drive.swerveDrive(0, 0.2, 0);*/
			} else if(Math.abs(LeftJoystick_X)+
			Math.abs(LeftJoystick_Y) +  Math.abs(RightJoystick_X) > 0) {
				Robot.drive.swerveDrive( 
					(LeftJoystick_X),
			 		(-LeftJoystick_Y),
			 		(RightJoystick_X));
			} else {
				Robot.drive.stopDriveMotors();
				Robot.drive.stopSteerMotors();				
			} 
			
		
			
			if(joystick0.getButtonPressed(1))
				Robot.gyro.resetGyro();


		}
	}
}
