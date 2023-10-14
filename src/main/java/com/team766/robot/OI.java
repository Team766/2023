package com.team766.robot;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.constants.InputConstants;
import com.team766.robot.procedures.*;
import com.team766.robot.mechanisms.Drive;
import com.team766.robot.mechanisms.Intake;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
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

	private static final double HighConeArm1 = 0;
	private static final double CHA2 = 0;
	private static final double CMA1 = 0;
	// enum generalControl{
	// 	CONE_HIGH_NODE,
	// 	CUBE_HIGH_NODE,
	// 	CONE_MID_NODE,
	// 	CUBE_MID_NODE,
	// 	OFF,
	// 	READY,
	// 	HUMANPLAYER_PICKUP,
	// 	MANUAL,
	// 	HYBRID_NODE
	// };
	
	// public generalControl generalState = generalControl.OFF;

	private static final double FINE_DRIVING_COEFFICIENT = 0.25;
	double turningValue = 0;
	boolean manualControl = true;
	
	public OI() {
		loggerCategory = Category.OPERATOR_INTERFACE;

		leftJoystick = RobotProvider.instance.getJoystick(InputConstants.LEFT_JOYSTICK);
		rightJoystick = RobotProvider.instance.getJoystick(InputConstants.RIGHT_JOYSTICK);
		controlPanel = RobotProvider.instance.getJoystick(InputConstants.CONTROL_PANEL);
	}
	
	public void run(Context context) {
		context.takeOwnership(Robot.drive);
		context.takeOwnership(Robot.intake);
		context.takeOwnership(Robot.gyro);

		while (true) {
			context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
			RobotProvider.instance.refreshDriverStationData();

			// Add driver controls here - make sure to take/release ownership
			// of mechanisms when appropriate.

			leftJoystickX = leftJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT);
			leftJoystickY = leftJoystick.getAxis(InputConstants.AXIS_FORWARD_BACKWARD);
			rightJoystickX = rightJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT);
			//Robot.drive.setGyro(-Robot.gyro.getGyroYaw());
			
			// if (DriverStation.getAlliance() == Alliance.Red) {
			// 	SmartDashboard.putString("Alliance", "RED");
			// } else if (DriverStation.getAlliance() == Alliance.Blue) {
			// 	SmartDashboard.putString("Alliance", "BLUE");
			// } else {
			// 	SmartDashboard.putString("Alliance", "NULLLLLLLLL");
			// }
			

			if (leftJoystick.getButtonPressed(InputConstants.RESET_GYRO)) {
				Robot.gyro.resetGyro();
			}

			if (leftJoystick.getButtonPressed(InputConstants.RESET_POS)) {
				Robot.drive.resetCurrentPosition();
			}

			if (Math.abs(rightJoystick.getAxis(InputConstants.AXIS_FORWARD_BACKWARD)) > 0.05) {
				RightJoystick_Y = rightJoystick.getAxis(InputConstants.AXIS_FORWARD_BACKWARD);
			} else {
				RightJoystick_Y = 0;
			}
			if (Math.abs(rightJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT)) > 0.05) {
				rightJoystickX = rightJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT) / 2;
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
			if (controlPanel.getButtonPressed(InputConstants.INTAKE)) {
				if (Robot.intake.getState() == Intake.State.IDLE) {
					Robot.intake.in();
				} else {
					Robot.intake.stop();
				}
			}

			if (controlPanel.getButtonPressed(InputConstants.OUTTAKE)) {
				if (Robot.intake.getState() == Intake.State.IDLE) {
					Robot.intake.out();
				} else {
					Robot.intake.stop();
				}
			} 

			// Sets the wheels to the cross position if the cross button is pressed
			if (rightJoystick.getButtonPressed(InputConstants.CROSS_WHEELS)) {
				if (!isCross) {
					context.startAsync(new SetCross());
				}
				isCross = !isCross;
			}
			

			SmartDashboard.putString("Alliance", DriverStation.getAlliance().toString());
			
			// Moves the robot if there are joystick inputs
			if (!isCross && Math.abs(leftJoystickX) + Math.abs(leftJoystickY) + Math.abs(rightJoystickX) > 0) {
				context.takeOwnership(Robot.drive);
				// If a button is pressed, drive is just fine adjustment
				if (leftJoystick.getButton(InputConstants.FINE_DRIVING)) {
					Robot.drive.controlFieldOriented(Math.toRadians(Robot.gyro.getGyroYaw()), (-leftJoystickX * FINE_DRIVING_COEFFICIENT), (leftJoystickY * FINE_DRIVING_COEFFICIENT), (-rightJoystickX * FINE_DRIVING_COEFFICIENT));
				} else {
          // On deafault, controls the robot field oriented
          // Need negatives here, controls backwards otherwise (most likely specific to CLR)
					Robot.drive.controlFieldOriented(Math.toRadians(Robot.gyro.getGyroYaw()), (-leftJoystickX), (leftJoystickY), (-rightJoystickX));
				}
			} else if (!isCross) {
				Robot.drive.stopDrive();			
			} 

			/* if (rightJoystick.getButtonPressed(InputConstants.CROSS_WHEELS)) {
				context.startAsync(new setCross());
			} */

			if (controlPanel.getButtonPressed(InputConstants.CONE_HIGH)) {
				log("Arm cone high");
			}
			if (controlPanel.getButtonPressed(InputConstants.CONE_MID)) {
				log("Arm cone mid");
			}
			if (controlPanel.getButtonPressed(InputConstants.ARM_READY)) {
				log("Arm ready");
			}
			if (controlPanel.getButtonPressed(InputConstants.HUMANPLAYER_PICKUP)) {
			}
			if (controlPanel.getButtonPressed(InputConstants.UNSTOWED)) {
				log("Arm unstowed");
			}
			/* if(controlPanel.getButton(InputConstants.IN_CHASSIS)){
				Robot.arms.pidForArmOne(22.73);
				Robot.arms.pidForArmTwo(-73.664);
			} */
			if (controlPanel.getButton(InputConstants.NUDGE_UP)) {
				log("Arm nudge up");
			}

			if (controlPanel.getButton(InputConstants.NUDGE_DOWN)) {
				log("Arm nudge down");
			}

			if (controlPanel.getButton(InputConstants.GRAB_IN)) {
			} else if (rightJoystick.getButton(InputConstants.GRABBER_RELEASE)) {
			} else {
			} 

			if (controlPanel.getButtonPressed(InputConstants.BRAKE)) {
			} else if (controlPanel.getButtonPressed(InputConstants.COAST)) {
			}

			if (leftJoystick.getButtonPressed(InputConstants.ARM_STOP)) {
			}

			if (controlPanel.getButtonPressed(13)) {
			}
		}
	}
}
