package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;
import com.team766.robot.constants.ChargeConstants;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * {@link Procedure} to use the gyro to automatically balance on the charge station.
 * See GyroBalance.md for more details
 */
public class GyroBalance extends Procedure {

	// State machine with 4 states for position on ramp
	private enum State {
		GROUND,
		RAMP_TRANSITION,
		RAMP_TILT,
		RAMP_LEVEL
	}

	// Direction determines which direction the robot moves
	private enum Direction {
		LEFT,
		RIGHT,
		STOP,
	}

	// tilt is the overall combination of pitch and roll
	private double tilt = Robot.gyro.getAbsoluteTilt();

	// absSpeed is unsigned speed value
	private double absSpeed;
	private State prevState;
	private State curState;
	private Direction direction;
	private final Alliance alliance;

	private final double TOP_TILT = 15.0;
	private final double FLAP_TILT = 11;

	// Tweak these values to adjust how the robot balances
	private final double LEVEL = 7;
	private final double CORRECTION_DELAY = 0.5;
	private final double SPEED_GROUND = .3;
	private final double SPEED_TRANSITION = .25;
	private final double SPEED_TILT = .18;

	/** 
	 * Constructor to create a new GyroBalance instance
	 * @param alliance Alliance for setting direction towards charge station
	*/
	public GyroBalance(Alliance alliance) {
		this.alliance = alliance;
	}

	public void run(Context context) {
		context.takeOwnership(Robot.gyro);
		context.takeOwnership(Robot.drive);

		// curX is current robot x position
		double curX = Robot.drive.getCurrentPosition().getX();
		Robot.drive.setGyro(Robot.gyro.getGyroYaw());

		// driveSpeed is actual value of speed passed into the swerveDrive method
		double driveSpeed = 1;

		// Sets movement direction ground state if on ground
		setDir(curX);

		// sets starting state if not on ground
		if (tilt < LEVEL && curState != State.GROUND) {
			curState = State.RAMP_LEVEL;
		} else if (tilt < TOP_TILT && tilt > FLAP_TILT) {
			curState = State.RAMP_TILT;
		} else if (tilt > LEVEL) {
			curState = State.RAMP_TRANSITION;
		}

		do {
			// Sets prevState to the current state and calculates curState
			prevState = curState;
			curX = Robot.drive.getCurrentPosition().getX();
			tilt = Robot.gyro.getAbsoluteTilt();
			log("curX:" + curX);
			log("direction: " + direction);
			setState();
			log("diretion:" + direction.toString());

			// Both being on Red alliance and needing to move right would make the movement direction negative, so this expression corrects for that
			if ((alliance == Alliance.Red) ^ (direction == Direction.RIGHT)) {
				driveSpeed = -absSpeed; 
			} else {
				driveSpeed = absSpeed;
			}

			// Drives the robot with the calculated speed and direction
			Robot.drive.swerveDrive(0, -driveSpeed, 0);
			context.yield();
		}
		// Loops until robot is level or until a call to the abort() method
		while (!(curState == State.RAMP_LEVEL));

		// After the robot is level, drives for correctionDelay seconds.
		// Direction is opposite due to inversion of speed in setState() so it corrects for overshooting
		context.waitForSeconds(CORRECTION_DELAY);
		
		// Locks wheels once balanced
		context.startAsync(new setCross());

		context.releaseOwnership(Robot.drive);
		context.releaseOwnership(Robot.gyro);
	} 

	// Sets state in state machine, see more details in GyroBalance.md
	private void setState() { 
		if (prevState == State.GROUND && tilt > LEVEL) {
			curState = State.RAMP_TRANSITION;
			absSpeed = SPEED_TRANSITION;
			log("Transition, prevState: " + prevState + ", curState: " + curState);
		} else if (prevState == State.RAMP_TRANSITION && tilt < TOP_TILT && tilt > FLAP_TILT) {
			curState = State.RAMP_TILT;
			absSpeed = SPEED_TILT;
			log("Tilt, prevState: " + prevState + ", curState: " + curState);
		} else if (prevState == State.RAMP_TILT && tilt < LEVEL) {
			curState = State.RAMP_LEVEL;
			// If level, sets speed to negative to correct for overshooting
			absSpeed = -absSpeed;
			log("Level, prevState: " + prevState + ", curState: " + curState);
		} 
		if (curState == State.GROUND) {
			absSpeed = SPEED_GROUND;
		}
	}

	/**
	 * Sets direction towards desired charge station
	 * If robot is level and outside of charge station boundaries, sets state to ground
	 * @param curX current robot x position
	 */
	private void setDir(double curX) {
		switch (alliance) {
			case Red:
				// If to the right of the charge station, go left
				if (curX > ChargeConstants.RED_BALANCE_TARGET_X) {
					// If level and outside of charge station boundaries, set state to ground
					if (tilt < LEVEL && curX > ChargeConstants.RED_RIGHT_PT) {
						curState = State.GROUND;
					}
					direction = Direction.LEFT;
				// If to the left of the charge station, go right
				} else {
					if (tilt < LEVEL && curX < ChargeConstants.RED_LEFT_PT) {
						curState = State.GROUND;
					}
					direction = Direction.RIGHT;
				}
				break;
			case Blue:
				// Same logic for blue alliance coordinates
				if (curX > ChargeConstants.BLUE_BALANCE_TARGET_X) {
					if (tilt < LEVEL && curX > ChargeConstants.BLUE_RIGHT_PT) {
						curState = State.GROUND;
					}
					direction = Direction.LEFT;
				} else {
					if (tilt < LEVEL && curX < ChargeConstants.BLUE_LEFT_PT) {
						curState = State.GROUND;
					}
					direction = Direction.RIGHT;
				}
				break;
			case Invalid: //drop down
			default: 
				log("Invalid alliance");
		} 
	}

}
