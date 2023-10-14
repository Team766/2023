package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

/**
 * Basic intake.  Mounted on end of {@link Wrist}.  The intake can be controlled to attempt to
 * pull a game piece in via {@link #in}, release a contained game piece via {@link #out}, or stop
 * moving via {@link #stop}.
 * 
 * Note: the Intake does not maintain any state as to whether or not it contains a game piece.
 */
public class Intake extends Mechanism {

	/**
	 * The current movement state for the intake.
	 */
	public enum State {
		IDLE,
		IN,
		OUT
	}
	
	private MotorController motor;
	private State state = State.IDLE;
	
	/**
	 * Constructs a new Intake.
	 */
	public Intake() {
		motor = RobotProvider.instance.getMotor("intake.motor");
	}

	/**
	 * Returns the current movement state of the intake.
	 * 
	 * @return The current movement state of the intake.
	 */
	public State getState() {
		return state;
	}

	/**
	 * Turns the intake motor on in order to pull a game piece into the mechanism.
	 */
	public void in() {
		checkContextOwnership();
		motor.set(1.0);
		state = State.IN;
	}

	/**
	 * Turns the intake motor on in reverse direction, to release any contained game piece.
	 */
	public void out() {
		checkContextOwnership();
		motor.set(-1.0);	
		state = State.OUT;
	}

	/**
	 * Turns off the intake motor.
	 */
	public void stop() {
		checkContextOwnership();
		motor.set(0.0);
		state = State.IDLE;
	}
}