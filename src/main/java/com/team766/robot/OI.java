package com.team766.robot;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.logging.Severity;
import com.team766.robot.constants.InputConstants;
import com.team766.robot.procedures.*;
import com.team766.simulator.interfaces.ElectricalDevice.Input;
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
	private JoystickReader boxopGamepad;
	private double rightJoystickX = 0;
	private double leftJoystickX = 0;
	private double leftJoystickY = 0;
	private boolean isCross = false;

	private static final double FINE_DRIVING_COEFFICIENT = 0.25;
	double turningValue = 0;
	boolean manualControl = true;
	
	public OI() {
		loggerCategory = Category.OPERATOR_INTERFACE;

		leftJoystick = RobotProvider.instance.getJoystick(InputConstants.LEFT_JOYSTICK);
		rightJoystick = RobotProvider.instance.getJoystick(InputConstants.RIGHT_JOYSTICK);
		boxopGamepad = RobotProvider.instance.getJoystick(InputConstants.BOXOP_GAMEPAD);
	}
	
	public void run(Context context) {
		context.takeOwnership(Robot.drive);
		// context.takeOwnership(Robot.intake);
		context.takeOwnership(Robot.gyro);

		PlacementPosition placementPosition = null;

		while (true) {
			context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
			RobotProvider.instance.refreshDriverStationData();

			SmartDashboard.putString("Alliance", DriverStation.getAlliance().toString());

			// Add driver controls here - make sure to take/release ownership
			// of mechanisms when appropriate.

			leftJoystickX = leftJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT);
			leftJoystickY = leftJoystick.getAxis(InputConstants.AXIS_FORWARD_BACKWARD);
			rightJoystickX = rightJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT);
			//Robot.drive.setGyro(-Robot.gyro.getGyroYaw());

			if (leftJoystick.getButtonPressed(InputConstants.INTAKE_OUT)) {
				new IntakeOut().run(context);
			} else if (leftJoystick.getButtonReleased(InputConstants.INTAKE_OUT)) {
				new IntakeStop().run(context);
			}
			
			if (leftJoystick.getButtonPressed(InputConstants.RESET_GYRO)) {
				Robot.gyro.resetGyro();
			}

			if (leftJoystick.getButtonPressed(InputConstants.RESET_POS)) {
				Robot.drive.resetCurrentPosition();
			}

			if (Math.abs(rightJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT)) > 0.05) {
				rightJoystickX = rightJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT) / 2;
			} else {
				rightJoystickX = 0;	
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

			// Sets the wheels to the cross position if the cross button is pressed
			if (rightJoystick.getButtonPressed(InputConstants.CROSS_WHEELS)) {
				if (!isCross) {
					context.startAsync(new SetCross());
				}
				isCross = !isCross;
			}
			
			// Moves the robot if there are joystick inputs
			if (!isCross && Math.abs(leftJoystickX) + Math.abs(leftJoystickY) + Math.abs(rightJoystickX) > 0) {
				context.takeOwnership(Robot.drive);
				// If a button is pressed, drive is just fine adjustment
				if (leftJoystick.getButton(InputConstants.FINE_DRIVING)) {
					Robot.drive.controlFieldOriented(Math.toRadians(Robot.gyro.getGyroYaw()), (leftJoystickX * FINE_DRIVING_COEFFICIENT), (leftJoystickY * FINE_DRIVING_COEFFICIENT), (rightJoystickX * FINE_DRIVING_COEFFICIENT));
				} else {
          	// On deafault, controls the robot field oriented
					Robot.drive.controlFieldOriented(Math.toRadians(Robot.gyro.getGyroYaw()), (leftJoystickX), (leftJoystickY), (rightJoystickX));
				}
			} else {
				Robot.drive.stopDrive();			
			}
			
			// Respond to boxop commands

			// first, check if the boxop is making a cone or cube selection
			if (boxopGamepad.getPOV() == InputConstants.POV_UP) {
				new GoForCones().run(context);
			} else if (boxopGamepad.getPOV() == InputConstants.POV_DOWN) {
				new GoForCubes().run(context);
			}

			// look for button presses to queue placement of intake/wrist/elevator superstructure
			if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_LOW)) {
				placementPosition = PlacementPosition.LOW_NODE;
				// TODO: update lights
			} else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_MID)) {
				placementPosition = PlacementPosition.MID_NODE;
				// TODO: update lights
			} else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_HIGH)) {
				placementPosition = PlacementPosition.HIGH_NODE;
				// TODO: update lights
			} else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_HUMAN_PLAYER)) {
				placementPosition = PlacementPosition.HUMAN_PLAYER;
				// TODO: update lights
			}

			// look for button hold to start intake, release to idle intake
			if (boxopGamepad.getButtonPressed(InputConstants.BUTTON_INTAKE_IN)) {
				new IntakeIn().run(context);
			} else if (boxopGamepad.getButtonReleased(InputConstants.BUTTON_INTAKE_IN)) {
				new IntakeIdle().run(context);
			} else if (boxopGamepad.getButton(InputConstants.BUTTON_INTAKE_STOP)) {
				new IntakeStop().run(context);
			}

			// look for button hold to extend intake/wrist/elevator superstructure,
			// release to retract
			if (boxopGamepad.getButtonPressed(InputConstants.BUTTON_EXTEND_WRISTVATOR)) {
				switch (placementPosition) {
					case LOW_NODE:
						new ExtendWristvatorToLow().run(context);
						break;
					case MID_NODE:
						new ExtendWristvatorToMid().run(context);
						break;
					case HIGH_NODE:
						new ExtendWristvatorToHigh().run(context);
						break;
					case HUMAN_PLAYER:
						new ExtendWristvatorToHuman().run(context);
						break;
					default:
					// warn, ignore
					log(Severity.WARNING, "Unexpected placement position: " + placementPosition.toString());
					break;
				}
			} else if (boxopGamepad.getButtonReleased(InputConstants.BUTTON_EXTEND_WRISTVATOR)) {
				new RetractWristvator().run(context);
			}

			// look for manual nudges
			// we only allow these if the extend elevator trigger is extended
			if (boxopGamepad.getButton(InputConstants.BUTTON_EXTEND_WRISTVATOR)) {

				// look for elevator nudges
				double elevatorNudgeAxis = boxopGamepad.getAxis(InputConstants.AXIS_ELEVATOR_MOVEMENT);
				if (Math.abs(elevatorNudgeAxis) > 0.05) {
					context.takeOwnership(Robot.elevator);
					if (elevatorNudgeAxis > 0) {
						Robot.elevator.nudgeUp();
					} else if (elevatorNudgeAxis < 0) {
						Robot.elevator.nudgeDown();
					}
					context.releaseOwnership(Robot.elevator);
				}

				// look for wrist nudges
				double wristNudgeAxis = boxopGamepad.getAxis(InputConstants.AXIS_WRIST_MOVEMENT);
				if (Math.abs(wristNudgeAxis) > 0.05) {
					context.takeOwnership(Robot.wrist);
					if (wristNudgeAxis > 0) {
						Robot.wrist.nudgeUp();
					} else if (wristNudgeAxis < 0) {
						Robot.wrist.nudgeDown();
					}
					context.releaseOwnership(Robot.wrist);
				}
			}
		}
	}
}