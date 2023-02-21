package com.team766.robot.mechanisms;

import com.ctre.phoenix.time.StopWatch;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.MotorController.ControlMode;
import com.team766.hal.wpilib.CANSparkMaxMotorController;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

public class ArmJoint {

	private Mechanism parent;
	private Logger logger;

	private MotorController jointMotor;
	private CANSparkMaxMotorController jointMotorEx;

	private RelativeEncoder motorEncoder;
	private SparkMaxAbsoluteEncoder motorAbsoluteEncoder;

	private StopWatch periodicLoggingStopwatch;

	private final float angleLimitMin;
	private final float angleLimitMax;


	/**
	 * Initialize joint, and pass in parameters from the parent
	 * 
	 * @param parent
	 * @param jointMotor
	 * @throws Exception
	 */
	public ArmJoint(Mechanism parent, MotorController jointMotor, float angleLimitMin, float angleLimitMax) throws Exception {
		if(parent == null) throw new Exception("parent cannot be null");
		if(jointMotor == null) throw new Exception("jointMotor cannot be null");
		if(!(jointMotor instanceof CANSparkMaxMotorController)) throw new Exception("only SparkMax is supported for jointMotor");

		logger = Logger.get(Category.MECHANISMS);
		this.angleLimitMax = angleLimitMax;
		this.angleLimitMin = angleLimitMin;

		this.jointMotor = jointMotor;
		this.jointMotorEx = (CANSparkMaxMotorController)jointMotor;

		this.parent = parent;

		motorEncoder = this.jointMotorEx.getEncoder();
		motorAbsoluteEncoder = this.jointMotorEx.getAbsoluteEncoder(SparkMaxAbsoluteEncoder.Type.kDutyCycle);

		periodicLoggingStopwatch.start();
	}


	/**
	 * Run periodic jobs
	 */
	public void run() {

		if(periodicLoggingStopwatch.getDurationMs() > 1000) {
			periodicLoggingStopwatch.start();

			// log motor encoder and abs encoder
			logger.logRaw(Severity.INFO, "enc=" + motorEncoder.getPosition() + " absEnc=" + motorAbsoluteEncoder.getPosition());
		}
	}


	public void resetMotorEncoderPosition() {
		motorEncoder.setPosition(0);
	}


	// TODO: maxvelocity
	// TODO: max acceleration

	public void setMotorPosition(double angle) {
		if(angle < angleLimitMin) {
			angle = angleLimitMin;
			logger.logRaw(Severity.ERROR, "Exceed MIN LIMIT with " + angle);
		} else if(angle > angleLimitMax) {
			angle = angleLimitMax;
			logger.logRaw(Severity.ERROR, "Exceed MAX LIMIT with " + angle);
		}

		jointMotor.set(ControlMode.Position, (double)degreesToEncoderUnits(angle));
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