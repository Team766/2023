package com.team766.robot.mechanisms;

import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.library.RateLimiter;
import com.team766.library.ValueProvider;
import com.team766.logging.Severity;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import static com.team766.robot.constants.ConfigConstants.*;

/**
 * Basic wrist mechanism.  Used in conjunction with the {@link Intake} and {@link Elevator}.
 * Can be moved up and down as part of teleop or autonomous control to move the {@link Intake}
 * (attached to the end of the Wrist) closer to a game piece or game element (eg node in the 
 * field, human player station), at which point the {@link Intake} can grab or release the game
 * piece as appropriate.
 */
public class Wrist extends Mechanism {

	/**
	 * Pre-set positions for the wrist.
	 */
	public enum Position {

		// TODO: adjust these values.

		/** Wrist is in top position.  Starting position. */
		TOP(-135),
		/** Wrist is in the position for moving around the field. */ 
		RETRACTED(-120.0),
		/** Wrist is level with ground. */
		LEVEL(0.0),
		HIGH_NODE(-30),
		MID_NODE(-25.5),
		/** Wrist is fully down. */ 
		BOTTOM(60);

		private final double angle;
		
		Position(double angle) {
			this.angle = angle;
		}

		private double getAngle() {
			return angle;
		}
	}

	private static final double NUDGE_INCREMENT = 5.0;
	private static final double NUDGE_DAMPENER = 0.15;

	private final CANSparkMax motor;
	private final SparkMaxPIDController pidController;
	private final ValueProvider<Double> pGain;
	private final ValueProvider<Double> iGain;
	private final ValueProvider<Double> dGain;
	private final ValueProvider<Double> ffGain;
	private final RateLimiter rateLimiter = new RateLimiter(1.0 /* seconds */);

	/**
	 * Contructs a new Wrist.
	 */
	public Wrist() {
		MotorController halMotor = RobotProvider.instance.getMotor(WRIST_MOTOR);
		if (!(halMotor instanceof CANSparkMax)) {
			log(Severity.ERROR, "Motor is not a CANSparkMax!");
			throw new IllegalStateException("Motor is not a CANSparkMax!");
		}
		motor = (CANSparkMax) halMotor;

		motor.getEncoder().setPosition(EncoderUtils.wristDegreesToRotations(Position.TOP.getAngle()));

		// stash the PIDController for convenience.  will update the PID values to the latest from the config
		// file each time we use the motor.
		pidController = motor.getPIDController();
		pidController.setFeedbackDevice(motor.getEncoder());

		// grab config values for PID.
		pGain = ConfigFileReader.getInstance().getDouble(WRIST_PGAIN);
		iGain = ConfigFileReader.getInstance().getDouble(WRIST_IGAIN);
		dGain = ConfigFileReader.getInstance().getDouble(WRIST_DGAIN);
		ffGain = ConfigFileReader.getInstance().getDouble(WRIST_FFGAIN);
	}

	public double getRotations() {
		return motor.getEncoder().getPosition();
	}

	/**
	 * Returns the current angle of the wrist.
	 */
	public double getAngle() {
		return EncoderUtils.wristRotationsToDegrees(motor.getEncoder().getPosition());
	}

	public void nudgeNoPID(double value) {
		checkContextOwnership();
		double clampedValue = MathUtil.clamp(value, -1, 1);
		clampedValue *= NUDGE_DAMPENER; // make nudges less forceful. TODO: make this non-linear
		motor.set(clampedValue);	
	}

	public void stopWrist() {
		checkContextOwnership();
		motor.set(0);
	}

	public void nudgeUp() {
		System.err.println("Nudging up.");
		double angle = getAngle();
		double targetAngle = Math.max(angle - NUDGE_INCREMENT, Position.TOP.getAngle());
		System.err.println("Target: " + targetAngle);

		rotate(targetAngle);
	}

	public void nudgeDown() {
		System.err.println("Nudging down.");
		double angle = getAngle();
		double targetAngle = Math.min(angle + NUDGE_INCREMENT, Position.BOTTOM.getAngle());
		rotate(targetAngle);
	}

	/** 
	 * Rotates the wrist to a pre-set {@link Position}.
	 */
	public void rotate(Position position) {
		rotate(position.getAngle());
	}

	/**
	 * Starts rotating the wrist to the specified angle.
	 * NOTE: this method returns immediately.  Check the current wrist position of the wrist
	 * with {@link #getAngle()}.
	 */
	public void rotate(double angle) {
		checkContextOwnership();

		System.err.println("Setting target angle to " + angle);
		// set the PID controller values with whatever the latest is in the config
		pidController.setP(pGain.get());
		pidController.setI(iGain.get());
		pidController.setD(dGain.get());
		// pidController.setFF(ffGain.get());
		double ff = ffGain.get() * Math.cos(Math.toRadians(angle));
		SmartDashboard.putNumber("[WRIST] ff", ff);
		SmartDashboard.putNumber("[WRIST] reference", angle);

		pidController.setOutputRange(-1, 1);

		// convert the desired target degrees to rotations
		double rotations = EncoderUtils.wristDegreesToRotations(angle);

		// set the reference point for the wrist
		pidController.setReference(rotations, ControlType.kPosition, 0, ff);
	}

	@Override
	public void run() {
		if (rateLimiter.next()) {
			SmartDashboard.putNumber("[WRIST] Angle", getAngle());
			SmartDashboard.putNumber("[WRIST] Rotations", getRotations());
		}
	}
}
