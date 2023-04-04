package com.team766.robot;

import java.io.IOException;

import com.team766.framework.Procedure;

import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.constants.InputConstants;
import com.team766.robot.constants.InputConstants.IntakeState;
import com.team766.robot.procedures.*;
import com.team766.simulator.interfaces.ElectricalDevice.Input;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.team766.robot.mechanisms.Drive;

/*
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
*/

public class OI extends Procedure {
	private JoystickReader leftJoystick;
	private JoystickReader rightJoystick;
	private JoystickReader controlPanel;
	private double rightJoystickX = 0;
	private double RightJoystick_Y = 0;
	private double RightJoystick_Z = 0;
	private double RightJoystick_Theta = 0;
	private double leftJoystickX = 0;
	private double leftJoystickY = 0;
	private double LeftJoystick_Z = 0;
	private double LeftJoystick_Theta = 0;
	private boolean isCross = false;
	private IntakeState intakeState = IntakeState.IDLE;

	private static final double FINE_DRIVING_COEFFICIENT = 0.25;

	
	double turningValue = 0;

	
	public OI() {
		loggerCategory = Category.OPERATOR_INTERFACE;

		leftJoystick = RobotProvider.instance.getJoystick(InputConstants.LEFT_JOYSTICK);
		rightJoystick = RobotProvider.instance.getJoystick(InputConstants.RIGHT_JOYSTICK);
		controlPanel = RobotProvider.instance.getJoystick(InputConstants.CONTROL_PANEL);
	}
	
	public void run(Context context) {
		context.takeOwnership(Robot.drive);
		context.takeOwnership(Robot.intake);
		context.takeOwnership(Robot.arms);
		context.takeOwnership(Robot.grabber);
		context.takeOwnership(Robot.storage);
		context.takeOwnership(Robot.gyro);
		context.takeOwnership(Robot.voltageController);

		CameraServer.startAutomaticCapture();

		while (true) {
			context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
			RobotProvider.instance.refreshDriverStationData();

			// Add driver controls here - make sure to take/release ownership
			// of mechanisms when appropriate.

			leftJoystickX = Drive.correctedJoysticks(leftJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT));
			leftJoystickY = Drive.correctedJoysticks(leftJoystick.getAxis(InputConstants.AXIS_FORWARD_BACKWARD));
			rightJoystickX = Drive.correctedJoysticks(rightJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT));
			Robot.drive.setGyro(-Robot.gyro.getGyroYaw());
			
			// if (DriverStation.getAlliance() == Alliance.Red) {
			// 	SmartDashboard.putString("Alliance", "RED");
			// } else if (DriverStation.getAlliance() == Alliance.Blue) {
			// 	SmartDashboard.putString("Alliance", "BLUE");
			// } else {
			// 	SmartDashboard.putString("Alliance", "NULLLLLLLLL");
			// }
			
			Robot.voltageController.displayPower();
			Robot.voltageController.powerWarning();
			Robot.voltageController.currentWarning();
			
			if (controlPanel.getButtonPressed(InputConstants.RESET_GYRO)) {
				Robot.gyro.resetGyro();
			}

			if (controlPanel.getButtonPressed(InputConstants.RESET_POS)) {
				Robot.drive.resetCurrentPosition();
			}

			if (Math.abs(rightJoystick.getAxis(InputConstants.AXIS_FORWARD_BACKWARD)) > 0.05) {
				RightJoystick_Y = rightJoystick.getAxis(InputConstants.AXIS_FORWARD_BACKWARD);
			} else {
				RightJoystick_Y = 0;
			}
			if (Math.abs(rightJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT)) > 0.05) {
				rightJoystickX = rightJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT)/2;
			} else {
				rightJoystickX = 0;	
			}
			if (Math.abs(rightJoystick.getAxis(InputConstants.AXIS_TWIST)) > 0.05) {
				RightJoystick_Theta = rightJoystick.getAxis(InputConstants.AXIS_TWIST);
			} else {
				RightJoystick_Theta = 0;
			}
			if (Math.abs(leftJoystick.getAxis(InputConstants.AXIS_FORWARD_BACKWARD)) > 0.05) {
				leftJoystickY = leftJoystick.getAxis(InputConstants.AXIS_FORWARD_BACKWARD);
			} else {
				leftJoystickY = 0;
			}
			if (Math.abs(leftJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT)) > 0.05) {
				leftJoystickX = leftJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT);
			} else {
				leftJoystickX = 0;
			}
			if (Math.abs(leftJoystick.getAxis(InputConstants.AXIS_TWIST)) > 0.05) {
				LeftJoystick_Theta = leftJoystick.getAxis(InputConstants.AXIS_TWIST);
			} else {
				LeftJoystick_Theta = 0;
			}

			// Sets intake state based on button pressed
			if (controlPanel.getButtonPressed(InputConstants.INTAKE)){
				if (intakeState == IntakeState.IDLE){
					Robot.intake.startIntake();
					Robot.storage.beltIn();
					intakeState = IntakeState.SPINNINGFWD;
				} else {
					Robot.intake.stopIntake();
					Robot.storage.beltIdle();
					intakeState = IntakeState.IDLE;
				}
			}
			if (controlPanel.getButtonPressed(InputConstants.INTAKE_PISTONLESS)){
				if (intakeState == IntakeState.IDLE){
					Robot.intake.intakePistonless();
					Robot.storage.beltIn();
					intakeState = IntakeState.SPINNINGREV;
				} else {
					Robot.intake.stopIntake();
					Robot.storage.beltIdle();
					intakeState = IntakeState.IDLE;
				}
			} 
			if (controlPanel.getButtonPressed(InputConstants.OUTTAKE)){
				if (intakeState == IntakeState.IDLE){
					Robot.intake.reverseIntake();
					Robot.storage.beltOut();
					intakeState = IntakeState.SPINNINGREV;
				} else {
					Robot.intake.stopIntake();
					Robot.storage.beltIdle();
					intakeState = IntakeState.IDLE;
				}
			} 

			// Sets the wheels to the cross position if the cross button is pressed
			if (rightJoystick.getButtonPressed(InputConstants.CROSS_WHEELS)) {
				if (!isCross) {
					context.startAsync(new setCross());
				}
				isCross = !isCross;
			}
			

			SmartDashboard.putString("Alliance", DriverStation.getAlliance().toString());
			
			// Moves the robot if there are joystick inputs
			if (!isCross && Math.abs(leftJoystickX) + Math.abs(leftJoystickY) + Math.abs(rightJoystickX) > 0) {
				context.takeOwnership(Robot.drive);
				// If a button is pressed, drive is just fine adjustment
				if (leftJoystick.getButton(InputConstants.FINE_DRIVING)) {
					Robot.drive.swerveDrive((leftJoystickX * FINE_DRIVING_COEFFICIENT), (-leftJoystickY * FINE_DRIVING_COEFFICIENT), (rightJoystickX * FINE_DRIVING_COEFFICIENT));
				} else {
					Robot.drive.swerveDrive((leftJoystickX), (-leftJoystickY), (rightJoystickX));
				}
			} else if (!isCross) {
				Robot.drive.stopDriveMotors();
				Robot.drive.stopSteerMotors();				
			} 

			// if (rightJoystick.getButtonPressed(InputConstants.CROSS_WHEELS)) {
			// 	context.startAsync(new setCross());
			// }

		}
	}
}
