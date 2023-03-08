package com.team766.robot.mechanisms;

import java.util.Objects;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.MotorController.ControlMode;
import com.team766.hal.wpilib.CANSparkMaxMotorController;
import com.team766.library.RateLimiter;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

/**
 * Control for a single Arm Joint
 * 
 * Reference: Spark Max Library:
 * https://codedocs.revrobotics.com/java/com/revrobotics/package-summary.html
 */
public class ArmJoint {

	private Mechanism parent;
	private Logger logger;

	private MotorController jointMotor;
	private CANSparkMaxMotorController jointMotorEx;
	private SparkMaxPIDController jointMotorPid;

	private RelativeEncoder motorEncoder;
	private SparkMaxAbsoluteEncoder motorAbsoluteEncoder;

	private RateLimiter periodicLoggingRate;

	private final ArmJointConfig config;


	/**
	 * Initialize joint, and pass in parameters from the parent
	 * 
	 * @param parent
	 * @param jointMotor
	 * @throws Exception
	 */
	public ArmJoint(Mechanism parent, MotorController jointMotor, ArmJointConfig config) {
		Objects.requireNonNull(parent, "parent cannot be null");
		Objects.requireNonNull(jointMotor, "jointMotor cannot be null");
		if(!(jointMotor instanceof CANSparkMaxMotorController)) throw new ClassCastException("only SparkMax is supported for jointMotor");

		logger = Logger.get(Category.MECHANISMS);

		// apply arm config
		this.config = config;

		this.jointMotor = jointMotor;
		this.jointMotorEx = (CANSparkMaxMotorController)jointMotor;
		this.jointMotorPid = jointMotorEx.getPIDController();

		this.parent = parent;

		motorEncoder = this.jointMotorEx.getEncoder();
		motorAbsoluteEncoder = this.jointMotorEx.getAbsoluteEncoder(SparkMaxAbsoluteEncoder.Type.kDutyCycle);

		periodicLoggingRate = new RateLimiter(1.000);

		// Apply config to motor driver
		
		jointMotorPid.setP(config.p);
		jointMotorPid.setI(config.i);
		jointMotorPid.setD(config.d);
		jointMotorPid.setFF(config.ff);
		jointMotorPid.setSmartMotionMaxVelocity(config.velocityMax, 0);
		jointMotorPid.setSmartMotionMinOutputVelocity(config.outputVelocityMin, 0);
		jointMotorPid.setSmartMotionMaxAccel(config.accelMax, 0);
		jointMotorPid.setOutputRange(config.powerMin, config.powerMax);

		// enable brake mode always
		jointMotorEx.setIdleMode(IdleMode.kBrake);

		//jointMotorEx.setClosedLoopRamp(0.5d);
		//jointMotorEx.setCurrentLimit(20.0d);
	}


	/**
	 * Run periodic jobs
	 */
	public void periodicUpdate() {

		if(periodicLoggingRate.next()) {
			// log motor encoder and abs encoder
			logger.logRaw(Severity.INFO, "enc=" + motorEncoder.getPosition() + " absEnc=" + motorAbsoluteEncoder.getPosition());
		}
	}


	public void resetMotorEncoderPosition() {
		parent.checkContextOwnership();

		motorEncoder.setPosition(0);
	}


	// TODO: maxvelocity
	// TODO: max acceleration

	public void setMotorPosition(double angle) {
		parent.checkContextOwnership();

		if(angle < config.angleMin) {
			angle = config.angleMin;
			logger.logRaw(Severity.ERROR, "Exceed MIN LIMIT with " + angle);
		} else if(angle > config.angleMax) {
			angle = config.angleMax;
			logger.logRaw(Severity.ERROR, "Exceed MAX LIMIT with " + angle);
		}

		jointMotor.set(ControlMode.Position, (double)degreesToEncoderUnits(angle));
		//jointMotorPid.setReference((double)degreesToEncoderUnits(angle), ControlType.kSmartMotion);

	}

	public double getMotorPosition() {
		return encoderUnitsToDegrees(motorEncoder.getPosition());
	}

	// changing degrees to encoder units for the motor encoder
	protected double degreesToEncoderUnits(double angle) {
		return angle * (44.0d / 90.0d);
	}

	// changing encoder units to degrees for the motor encoder
	protected double encoderUnitsToDegrees(double units) {
		return units / (44.0d / 90.0d);
	}

	// TODO: test following w/ abs enc 
}