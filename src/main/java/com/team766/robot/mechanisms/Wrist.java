package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.revrobotics.CANSparkMax;

/**
 * Basic wrist mechanism.  Can be moved up and down as part of teleop or autonomous control to
 * move the {@link Intake} (attached to the end of the Wrist) closer to a game piece or game 
 * element (eg node in the field, human player station).
 */
public class Wrist extends Mechanism {

	public enum Position {

		// TODO: move these angles to a constants class.
		LOW(0), 
		MID(45), 
		HIGH(180);

		private final int angle;
		
		Position(int angle) {
			this.angle = angle;
		}

		private int getAngle() {
			return angle;
		}
	}

	private final CANSparkMax motor;

	/**
	 * Contructs a new Wrist.
	 */
	public Wrist() {
		motor = (CANSparkMax) RobotProvider.instance.getMotor("wrist.motor");
	}

	public void rotate(Position position) {
		rotate(position.getAngle());
	}

	public void rotate(int angle) {
		checkContextOwnership();

		// TODO: add logic here.
	}
}
