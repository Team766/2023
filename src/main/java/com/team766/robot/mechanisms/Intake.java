package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.library.RateLimiter;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import static com.team766.robot.constants.ConfigConstants.*;

/**
 * Basic intake.  Mounted on end of {@link Wrist}.  The intake can be controlled to attempt to
 * pull a game piece in via {@link #in}, release a contained game piece via {@link #out}, or stop
 * moving via {@link #stop}.
 * 
 * 
 * While the Intake does not maintain any state as to whether or not it contains a game piece,
 * it does have different modes of operation based on what kind of game piece it is prepared to
 * intake or outtake.  This is because the motor must spin in opposite directions to intake cubes
 * versus cones.
 */
public class Intake extends Mechanism {

	private static final double POWER_IN = 0.3;
	private static final double POWER_OUT = 0.25;
	private static final double POWER_IDLE = 0.05;

	/**
	 * The current type of game piece the Intake is preparing to hold or is holding.
	 */
	public enum GamePieceType {
		CONE,
		CUBE
	}

	/**
	 * The current movement state for the intake.
	 */
	public enum State {
		STOPPED,
		IDLE,
		IN,
		OUT
	}
	
	private MotorController motor;
	private GamePieceType gamePieceType = GamePieceType.CONE;
	private State state = State.IDLE;
	private RateLimiter rateLimiter = new RateLimiter(5 /* seconds */);
	
	/**
	 * Constructs a new Intake.
	 */
	public Intake() {
		motor = RobotProvider.instance.getMotor(INTAKE_MOTOR);
	}

	/**
	 * Returns the type of game piece the Intake is preparing to hold or is holding.
	 * @return The current game piece type.
	 */
	public GamePieceType getGamePieceType() {
		return gamePieceType;
	}

	/**
	 * Sets the type of game piece type the Intake is preparing to hold or is holding.
	 */
	public void setGamePieceType(GamePieceType type) {
		checkContextOwnership();

		this.gamePieceType = type;
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

		double power = (gamePieceType == GamePieceType.CONE) ? POWER_IN : (-1 * POWER_IN);
		motor.set(power);
		state = State.IN;
	}

	/**
	 * Turns the intake motor on in reverse direction, to release any contained game piece.
	 */
	public void out() {
		checkContextOwnership();

		double power = (gamePieceType == GamePieceType.CONE) ? (-1 * POWER_OUT) : POWER_OUT;
		motor.set(power);	
		state = State.OUT;
	}

	/**
	 * Turns off the intake motor.
	 */
	public void stop() {
		checkContextOwnership();
		motor.set(0.0);
		state = State.STOPPED;
	}

	/**
	 * Turns the intake to idle - run at low power to keep the game piece contained.
	 */
	public void idle() {
		checkContextOwnership();

		double power = (gamePieceType == GamePieceType.CONE) ? POWER_IDLE : (-1 * POWER_IDLE);
		motor.set(power);
		state = State.IDLE;
	}

	@Override
	public void run() {
		if (rateLimiter.next()) {
			SmartDashboard.putString("[INTAKE] Game Piece", gamePieceType.toString());
			SmartDashboard.putString("[INTAKE] State", state.toString());
		}
	}
}