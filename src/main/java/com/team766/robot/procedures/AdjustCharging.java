package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.odometry.PointDir;
import com.team766.robot.Robot;
import com.team766.robot.constants.ChargeConstants;
import edu.wpi.first.wpilibj.DriverStation.Alliance;


public class AdjustCharging extends Procedure {

	//TODO: Make code not suck

	private enum State {
		GROUND,
		RAMP_TRANSITION,
		RAMP_TILT,
		RAMP_LEVEL
	}

	private enum Direction {
		LEFT,
		RIGHT,
		STOP,
	}

	private double tilt = Robot.gyro.getAbsoluteTilt();
	private double speed;
	private State prevState;
	private State curState;
	private Direction direction;
	
	private final Alliance alliance;

	private final double LEVEL = 9;
	private final double TOP_TILT = 15.0;
	private final double FLAP_TILT = 11;

	private final double SPEED_GROUND = .3;
	private final double SPEED_TRANSITION = .25;
	private final double SPEED_TILT = .22;

	public AdjustCharging(Alliance alliance) {
		this.alliance = alliance;
	}

	public void run(Context context) {
		context.takeOwnership(Robot.gyro);
		context.takeOwnership(Robot.drive);
		Robot.drive.setCurrentPosition(new PointDir(6, 2.6)); // DELETE THIS ONCE DONE TESTING THIS WILL BE VERY BAD IF WE DONT
		double curX = Robot.drive.getCurrentPosition().getX();
		double driveSpeed = 1;


		// Sets movement direction and if on ground
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
			prevState = curState;
			curX = Robot.drive.getCurrentPosition().getX();
			tilt = Robot.gyro.getAbsoluteTilt();
			setState(curX);

			if ((alliance == Alliance.Red) ^ (direction == Direction.RIGHT)) {
				driveSpeed = -speed; 
			} else {
				driveSpeed = speed;
			}

			log("Pos: " + Robot.drive.getCurrentPosition());
			log("Direction: " + direction);
			log("tilt: " + tilt);
			log("Current state:" + curState + ", previous state: " + prevState);
			log("driveSpeed: " + driveSpeed);
			log("normal speed: " + speed);

			Robot.drive.swerveDrive(0, driveSpeed, 0);
			context.yield();
		}
		while (!(curState == State.RAMP_LEVEL));
		log("driveSpeed: " + driveSpeed);
		log("normal speed: " + speed);

		context.waitForSeconds(.25);
		log("driveSpeed: " + driveSpeed);
		log("normal speed: " + speed);
		log("trying to go back");
		
		context.startAsync(new setCross());

		context.releaseOwnership(Robot.drive);
		context.releaseOwnership(Robot.gyro);
	} 

	private void setState(double curX) { //TODO: Check robot behavior falling off of ramp
		if (prevState == State.GROUND && tilt > LEVEL) {
			curState = State.RAMP_TRANSITION;
			speed = SPEED_TRANSITION;
			log("Transition, prevState: " + prevState + ", curState: " + curState);
		} else if (prevState == State.RAMP_TRANSITION && tilt < TOP_TILT && tilt > FLAP_TILT) {
			curState = State.RAMP_TILT;
			speed = SPEED_TILT;
			log("Tilt, prevState: " + prevState + ", curState: " + curState);
		} else if (prevState == State.RAMP_TILT && tilt < LEVEL) {
			curState = State.RAMP_LEVEL;
			speed = -speed;
			log("Level, prevState: " + prevState + ", curState: " + curState);
		} else if (prevState == State.RAMP_LEVEL && tilt > LEVEL) {
			curState = State.RAMP_TILT;
			speed = SPEED_TILT;
			log("back into tilt, prevState: " + prevState + ", curState: " + curState);
		}
		if (curState == State.GROUND) {
			speed = SPEED_GROUND;
		}
	}

	private void setDir(double curX) {
		switch (alliance) {
			case Red:
				if (curX > ChargeConstants.RED_BALANCE_TARGET_X) {
					if (tilt < LEVEL && curX > ChargeConstants.RED_RIGHT_PT) {
						curState = State.GROUND;
						log("curX: " + curX);
					}
					direction = Direction.LEFT;
				} else {
					if (tilt < LEVEL && curX < ChargeConstants.RED_LEFT_PT) {
						curState = State.GROUND;
						log("curX: " + curX);
					}
					direction = Direction.RIGHT;
				}
				break;
			case Blue:
				if (curX > ChargeConstants.BLUE_BALANCE_TARGET_X) {
					if (tilt < LEVEL && curX > ChargeConstants.BLUE_RIGHT_PT) {
						curState = State.GROUND;
						log("curX: " + curX);
					}
					direction = Direction.LEFT;
				} else {
					if (tilt < LEVEL && curX < ChargeConstants.BLUE_LEFT_PT) {
						curState = State.GROUND;
						log("curX: " + curX);
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
