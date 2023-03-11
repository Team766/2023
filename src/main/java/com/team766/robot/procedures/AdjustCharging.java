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

	private final double LEVEL = 3.0;
	private final double TOP_TILT = 15.0;
	private final double FLAP_LEVEL = 34.0;
	private final double FLAP_TILT = 11;

	private final double SPEED_GROUND = .5;
	private final double SPEED_TRANSITION = .4;
	private final double SPEED_TILT = .2;

	public AdjustCharging(Alliance alliance) {
		this.alliance = alliance;
	}

	public void run(Context context) {
		context.takeOwnership(Robot.gyro);
		context.takeOwnership(Robot.drive);
		double curX = Robot.drive.getCurrentPosition().getX();


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
			setState(curX);
			tilt = Robot.gyro.getAbsoluteTilt();

			if (alliance == Alliance.Red ^ direction == Direction.LEFT) {
				speed *= -1;
			}

			log("Direction: " + direction);
			log("tilt: " + tilt);
			log("Current state:" + curState + ", previous state: " + prevState);

			Robot.drive.swerveDrive(0, speed, 0);
			context.yield();
		}
		while (!(curState == State.RAMP_LEVEL && prevState == State.RAMP_LEVEL));

		context.startAsync(new setCross());

		context.releaseOwnership(Robot.drive);
		context.releaseOwnership(Robot.gyro);
	} 

	private void setState(double curX) { //TODO: Check robot behavior falling off of ramp
		if (prevState == State.GROUND && tilt > LEVEL) {
			curState = State.RAMP_TRANSITION;
			speed = SPEED_TRANSITION;
		} else if (prevState == State.RAMP_TRANSITION && tilt < TOP_TILT && tilt > FLAP_TILT) {
			curState = State.RAMP_TILT;
			speed = SPEED_TILT;
		} else if (prevState == State.RAMP_TILT && tilt < LEVEL) {
			curState = State.RAMP_LEVEL;
			speed = 0;
		} else if (prevState == State.RAMP_LEVEL && tilt > LEVEL) {
			curState = State.RAMP_TILT;
			speed = SPEED_TILT;
		}
		setDir(curX);
	}

	private void setDir(double curX) {
		switch (alliance) {
			case Red:
				if (curX > ChargeConstants.RED_BALANCE_TARGET_X) {
					if (tilt < LEVEL && curX > ChargeConstants.RED_RIGHT_PT) {
						curState = State.GROUND;
					}
					direction = Direction.LEFT;
				} else {
					if (tilt < LEVEL && curX < ChargeConstants.RED_LEFT_PT) {
						curState = State.GROUND;
					}
					direction = Direction.RIGHT;
				}
				break;
			case Blue:
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
