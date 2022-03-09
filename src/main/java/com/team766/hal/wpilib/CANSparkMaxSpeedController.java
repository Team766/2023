package com.team766.hal.wpilib;

import java.util.function.Consumer;
import java.util.function.Supplier;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAnalogSensor;
import com.team766.hal.CANSpeedController;
import com.team766.hal.SpeedControllerCommandFailedException;

public class CANSparkMaxSpeedController extends CANSparkMax implements CANSpeedController {

	private Supplier<Double> sensorPositionSupplier;
	private Supplier<Double> sensorVelocitySupplier;
	private Consumer<Integer> sensorPositionSetter;
	private Consumer<Boolean> sensorInvertedSetter;
	private boolean sensorInverted = false;

	public CANSparkMaxSpeedController(int deviceId) {
		super(deviceId, MotorType.kBrushless);

		// Set default feedback device. This ensures that our implementations of
		// getSensorPosition/getSensorVelocity return values that match what the
		// device's PID controller is using.
		setSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
	}

	private void revErrorToException(REVLibError err) {
		if (err == REVLibError.kOk) {
			return;
		}
		throw new SpeedControllerCommandFailedException(err.toString());
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
			default:
				throw new IllegalArgumentException("Unsupported control mode " + mode);
		}
	}

	@Override
	public void setPosition(int position) {
		sensorPositionSetter.accept(position);
	}

	@Override
	public void follow(CANSpeedController leader) {
		try {
			revErrorToException(super.follow((CANSparkMax)leader));
		} catch (ClassCastException ex) {
			throw new IllegalArgumentException("Spark Max can only follow another Spark Max", ex);
		}
	}

	@Override
	public void setNeutralMode(NeutralMode neutralMode) {
		switch (neutralMode) {
			case Brake:
				revErrorToException(setIdleMode(IdleMode.kBrake));
			case Coast:
				revErrorToException(setIdleMode(IdleMode.kCoast));
			default:
				throw new IllegalArgumentException("Unsupported neutral mode " + neutralMode);
		}
	}

	@Override
	public void setP(double value) {
		revErrorToException(getPIDController().setP(value));
	}

	@Override
	public void setI(double value) {
		revErrorToException(getPIDController().setI(value));
	}

	@Override
	public void setD(double value) {
		revErrorToException(getPIDController().setD(value));
	}

	@Override
	public void setFF(double value) {
		revErrorToException(getPIDController().setFF(value));
	}

	@Override
	public void setSelectedFeedbackSensor(FeedbackDevice feedbackDevice) {
		switch (feedbackDevice) {
			case Analog: {
				SparkMaxAnalogSensor analog = getAnalog(SparkMaxAnalogSensor.Mode.kAbsolute);
				revErrorToException(analog.setInverted(sensorInverted));
				sensorPositionSupplier = analog::getPosition;
				sensorVelocitySupplier = analog::getVelocity;
				sensorPositionSetter = (pos) -> {};
				sensorInvertedSetter = analog::setInverted;
				revErrorToException(getPIDController().setFeedbackDevice(analog));
				return;
			}
			case CTRE_MagEncoder_Absolute:
				throw new IllegalArgumentException("SparkMax does not support CTRE Mag Encoder");
			case CTRE_MagEncoder_Relative:
				throw new IllegalArgumentException("SparkMax does not support CTRE Mag Encoder");
			case IntegratedSensor: {
				RelativeEncoder encoder = getEncoder();
				revErrorToException(encoder.setInverted(sensorInverted));
				sensorPositionSupplier = encoder::getPosition;
				sensorVelocitySupplier = encoder::getVelocity;
				sensorPositionSetter = encoder::setPosition;
				sensorInvertedSetter = encoder::setInverted;
				revErrorToException(getPIDController().setFeedbackDevice(encoder));
				return;
			}
			case None:
				return;
			case PulseWidthEncodedPosition:
				throw new IllegalArgumentException("SparkMax does not support PWM sensors");
			case QuadEncoder: {
				// TODO: should we pass a real counts-per-rev scale here?
				RelativeEncoder encoder = getAlternateEncoder(1);
				revErrorToException(encoder.setInverted(sensorInverted));
				sensorPositionSupplier = encoder::getPosition;
				sensorVelocitySupplier = encoder::getVelocity;
				sensorPositionSetter = encoder::setPosition;
				sensorInvertedSetter = encoder::setInverted;
				revErrorToException(getPIDController().setFeedbackDevice(encoder));
				return;
			}
			case RemoteSensor0:
				throw new IllegalArgumentException("SparkMax does not support remote sensors");
			case RemoteSensor1:
				throw new IllegalArgumentException("SparkMax does not support remote sensors");
			case SensorDifference:
				throw new IllegalArgumentException("SparkMax does not support SensorDifference");
			case SensorSum:
				throw new IllegalArgumentException("SparkMax does not support SensorSum");
			case SoftwareEmulatedSensor:
				throw new IllegalArgumentException("SparkMax does not support SoftwareEmulatedSensor");
			case Tachometer:
				throw new IllegalArgumentException("SparkMax does not support Tachometer");
			default:
				throw new IllegalArgumentException("Unsupported sensor type " + feedbackDevice);
		}
	}

	@Override
	public void setSensorInverted(boolean inverted) {
		sensorInverted = inverted;
		sensorInvertedSetter.accept(inverted);
	}

	@Override
	public void setOutputRange(double minOutput, double maxOutput) {
		revErrorToException(getPIDController().setOutputRange(minOutput, maxOutput));
	}

	@Override
	public void restoreFactoryDefault() {
		revErrorToException(restoreFactoryDefaults());
	}

	@Override
	public void setOpenLoopRamp(double secondsFromNeutralToFull) {
		revErrorToException(setOpenLoopRampRate(secondsFromNeutralToFull));
	}

	@Override
	public void setClosedLoopRamp(double secondsFromNeutralToFull) {
		revErrorToException(setClosedLoopRampRate(secondsFromNeutralToFull));
	}
	
}
