package com.team766.hal.wpilib;

import java.util.function.Function;
import java.util.function.Supplier;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAnalogSensor;
import com.team766.hal.MotorController;
import com.team766.hal.MotorControllerCommandFailedException;
import com.team766.logging.LoggerExceptionUtils;

public class CANSparkMaxMotorController extends CANSparkMax implements MotorController {

	private Supplier<Double> sensorPositionSupplier;
	private Supplier<Double> sensorVelocitySupplier;
	private Function<Double, REVLibError> sensorPositionSetter;
	private Function<Boolean, REVLibError> sensorInvertedSetter;
	private boolean sensorInverted = false;

	public CANSparkMaxMotorController(int deviceId) {
		super(deviceId, MotorType.kBrushless);

		// Set default feedback device. This ensures that our implementations of
		// getSensorPosition/getSensorVelocity return values that match what the
		// device's PID controller is using.
		setSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
	}

	private static enum ExceptionTarget {
		THROW,
		LOG,
	}

	private static void revErrorToException(ExceptionTarget throwEx, REVLibError err) {
		if (err == REVLibError.kOk) {
			return;
		}
		var ex = new MotorControllerCommandFailedException(err.toString());
		switch (throwEx) {
			case THROW:
				throw ex;
			case LOG:
				LoggerExceptionUtils.logException(ex);
				break;
		}
	}

	@Override
	public double getSensorPosition() {
		return sensorPositionSupplier.get();
	}

	@Override
	public double getSensorVelocity() {
		return sensorVelocitySupplier.get();
	}

	@Override
	public void set(ControlMode mode, double value) {
		switch (mode) {
			case Current:
				getPIDController().setReference(value, CANSparkMax.ControlType.kCurrent);
				break;
			case Disabled:
				disable();
				break;
			case Follower:
				throw new IllegalArgumentException("Use follow() method instead");
			case MotionMagic:
				throw new IllegalArgumentException("SparkMax does not support MotionMagic");
			case MotionProfile:
				throw new IllegalArgumentException("SparkMax does not support MotionProfile");
			case MotionProfileArc:
				throw new IllegalArgumentException("SparkMax does not support MotionProfileArc");
			case PercentOutput:
				getPIDController().setReference(value, CANSparkMax.ControlType.kDutyCycle);
				break;
			case Position:
				getPIDController().setReference(value, CANSparkMax.ControlType.kPosition);
				break;
			case Velocity:
				getPIDController().setReference(value, CANSparkMax.ControlType.kVelocity);
				break;
			case Voltage:
				getPIDController().setReference(value, CANSparkMax.ControlType.kVoltage);
			default:
				throw new IllegalArgumentException("Unsupported control mode " + mode);
		}
	}

	@Override
	public void setSensorPosition(double position) {
		revErrorToException(ExceptionTarget.THROW, sensorPositionSetter.apply(position));
	}

	@Override
	public void follow(MotorController leader) {
		try {
			revErrorToException(ExceptionTarget.LOG, super.follow((CANSparkMax)leader));
		} catch (ClassCastException ex) {
			LoggerExceptionUtils.logException(new IllegalArgumentException("Spark Max can only follow another Spark Max", ex));
		}
	}

	@Override
	public void setNeutralMode(NeutralMode neutralMode) {
		switch (neutralMode) {
			case Brake:
				revErrorToException(ExceptionTarget.LOG, setIdleMode(IdleMode.kBrake));
			case Coast:
				revErrorToException(ExceptionTarget.LOG, setIdleMode(IdleMode.kCoast));
			default:
				LoggerExceptionUtils.logException(new IllegalArgumentException("Unsupported neutral mode " + neutralMode));
		}
	}

	@Override
	public void setP(double value) {
		revErrorToException(ExceptionTarget.LOG, getPIDController().setP(value));
	}

	@Override
	public void setI(double value) {
		revErrorToException(ExceptionTarget.LOG, getPIDController().setI(value));
	}

	@Override
	public void setD(double value) {
		revErrorToException(ExceptionTarget.LOG, getPIDController().setD(value));
	}

	@Override
	public void setFF(double value) {
		revErrorToException(ExceptionTarget.LOG, getPIDController().setFF(value));
	}

	@Override
	public void setSelectedFeedbackSensor(FeedbackDevice feedbackDevice) {
		switch (feedbackDevice) {
			case Analog: {
				SparkMaxAnalogSensor analog = getAnalog(SparkMaxAnalogSensor.Mode.kAbsolute);
				revErrorToException(ExceptionTarget.LOG, analog.setInverted(sensorInverted));
				sensorPositionSupplier = analog::getPosition;
				sensorVelocitySupplier = analog::getVelocity;
				sensorPositionSetter = (pos) -> REVLibError.kOk;
				sensorInvertedSetter = analog::setInverted;
				revErrorToException(ExceptionTarget.LOG, getPIDController().setFeedbackDevice(analog));
				return;
			}
			case CTRE_MagEncoder_Absolute:
				LoggerExceptionUtils.logException(new IllegalArgumentException("SparkMax does not support CTRE Mag Encoder"));
			case CTRE_MagEncoder_Relative:
				LoggerExceptionUtils.logException(new IllegalArgumentException("SparkMax does not support CTRE Mag Encoder"));
			case IntegratedSensor: {
				RelativeEncoder encoder = getEncoder();
				// NOTE(rcahoon, 2022-04-19): Don't call this. Trying to call setInverted on the integrated sensor returns an error.
				// revErrorToException(ExceptionTarget.LOG, encoder.setInverted(sensorInverted));
				sensorPositionSupplier = encoder::getPosition;
				sensorVelocitySupplier = encoder::getVelocity;
				sensorPositionSetter = encoder::setPosition;
				// NOTE(rcahoon, 2022-04-19): Don't call this. Trying to call setInverted on the integrated sensor returns an error.
				// sensorInvertedSetter = encoder::setInverted;
				sensorInvertedSetter = (inverted) -> REVLibError.kOk;
				revErrorToException(ExceptionTarget.LOG, getPIDController().setFeedbackDevice(encoder));
				return;
			}
			case None:
				return;
			case PulseWidthEncodedPosition:
				LoggerExceptionUtils.logException(new IllegalArgumentException("SparkMax does not support PWM sensors"));
			case QuadEncoder: {
				// TODO: should we pass a real counts-per-rev scale here?
				RelativeEncoder encoder = getAlternateEncoder(1);
				revErrorToException(ExceptionTarget.LOG, encoder.setInverted(sensorInverted));
				sensorPositionSupplier = encoder::getPosition;
				sensorVelocitySupplier = encoder::getVelocity;
				sensorPositionSetter = encoder::setPosition;
				sensorInvertedSetter = encoder::setInverted;
				revErrorToException(ExceptionTarget.LOG, getPIDController().setFeedbackDevice(encoder));
				return;
			}
			case RemoteSensor0:
				LoggerExceptionUtils.logException(new IllegalArgumentException("SparkMax does not support remote sensors"));
			case RemoteSensor1:
				LoggerExceptionUtils.logException(new IllegalArgumentException("SparkMax does not support remote sensors"));
			case SensorDifference:
				LoggerExceptionUtils.logException(new IllegalArgumentException("SparkMax does not support SensorDifference"));
			case SensorSum:
				LoggerExceptionUtils.logException(new IllegalArgumentException("SparkMax does not support SensorSum"));
			case SoftwareEmulatedSensor:
				LoggerExceptionUtils.logException(new IllegalArgumentException("SparkMax does not support SoftwareEmulatedSensor"));
			case Tachometer:
				LoggerExceptionUtils.logException(new IllegalArgumentException("SparkMax does not support Tachometer"));
			default:
				LoggerExceptionUtils.logException(new IllegalArgumentException("Unsupported sensor type " + feedbackDevice));
		}
	}

	@Override
	public void setSensorInverted(boolean inverted) {
		sensorInverted = inverted;
		revErrorToException(ExceptionTarget.LOG, sensorInvertedSetter.apply(inverted));
	}

	@Override
	public void setOutputRange(double minOutput, double maxOutput) {
		revErrorToException(ExceptionTarget.LOG, getPIDController().setOutputRange(minOutput, maxOutput));
	}

	public void setCurrentLimit(double ampsLimit) {
		revErrorToException(ExceptionTarget.LOG, setSmartCurrentLimit((int)ampsLimit));
	}

	@Override
	public void restoreFactoryDefault() {
		revErrorToException(ExceptionTarget.LOG, restoreFactoryDefaults());
	}

	@Override
	public void setOpenLoopRamp(double secondsFromNeutralToFull) {
		revErrorToException(ExceptionTarget.LOG, setOpenLoopRampRate(secondsFromNeutralToFull));
	}

	@Override
	public void setClosedLoopRamp(double secondsFromNeutralToFull) {
		revErrorToException(ExceptionTarget.LOG, setClosedLoopRampRate(secondsFromNeutralToFull));
	}
	
}
