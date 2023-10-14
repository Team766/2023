package com.team766.robot.mechanisms;
import com.revrobotics.CANSparkMax;
import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;

public class Elevator extends Mechanism {
	public enum Position {

		// TODO: move these positions to a constants class.
		LOW(0), MID(45), HIGH(180), HUMAN(200);

		private final int position;

		Position(int position) {
			this.position = position;
		}

		private int getPosition() {
			return position;
		}
	}

	private final CANSparkMax leftMotor;
	private final CANSparkMax rightMotor;
	
	public Elevator() {
		leftMotor = (CANSparkMax) RobotProvider.instance.getMotor("elevator.leftMotor");
		rightMotor = (CANSparkMax) RobotProvider.instance.getMotor("elevator.rightMotor");

		rightMotor.follow(leftMotor);
	}

	public void moveTo(Position position) {
		moveTo(position.getPosition());
	}

	public void moveTo(int position) {
		checkContextOwnership();

		// TODO: add logic here.
	}
}
