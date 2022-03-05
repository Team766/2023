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

	Supplier<Double> sensorPositionSupplier;
	Supplier<Double> sensorVelocitySupplier;
	Consumer<Integer> sensorPositionSetter;

	public CANSparkMaxSpeedController(int deviceId) {
		super(deviceId, MotorType.kBrushless);

		// Set default feedback device. This ensures that our implementations of
		// getSensorPosition/getSensorVelocity return values that match what the
		// device's PID controller is using.
		configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
	}

	private void revErrorToException(REVLibError err) {
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
				getPIDController().setReference(value, ControlType.kCurrent);
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
				getPIDController().setReference(value, ControlType.kDutyCycle);
				break;
			case Position:
				getPIDController().setReference(value, ControlType.kPosition);
				break;
			case Velocity:
				getPIDController().setReference(value, ControlType.kVelocity);
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
	public void config_kP(int slotIdx, double value) {
		revErrorToException(getPIDController().setP(value, slotIdx));
	}

	@Override
	public void config_kI(int slotIdx, double value) {
		revErrorToException(getPIDController().setI(value, slotIdx));
	}

	@Override
	public void config_kD(int slotIdx, double value) {
		revErrorToException(getPIDController().setD(value, slotIdx));
	}

	@Override
	public void config_kF(int slotIdx, double value) {
		revErrorToException(getPIDController().setFF(value, slotIdx));
	}

	@Override
	public void configSelectedFeedbackSensor(FeedbackDevice feedbackDevice) {
		switch (feedbackDevice) {
			case Analog: {
				SparkMaxAnalogSensor analog = getAnalog(SparkMaxAnalogSensor.Mode.kAbsolute);
				sensorPositionSupplier = analog::getPosition;
				sensorVelocitySupplier = analog::getVelocity;
				sensorPositionSetter = (pos) -> {};
				revErrorToException(getPIDController().setFeedbackDevice(analog));
				return;
			}
			case CTRE_MagEncoder_Absolute:
				throw new IllegalArgumentException("SparkMax does not support CTRE Mag Encoder");
			case CTRE_MagEncoder_Relative:
				throw new IllegalArgumentException("SparkMax does not support CTRE Mag Encoder");
			case IntegratedSensor: {
				RelativeEncoder encoder = getEncoder();
				sensorPositionSupplier = encoder::getPosition;
				sensorVelocitySupplier = encoder::getVelocity;
				sensorPositionSetter = encoder::setPosition;
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
				sensorPositionSupplier = encoder::getPosition;
				sensorVelocitySupplier = encoder::getVelocity;
				sensorPositionSetter = encoder::setPosition;
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
	public void configNominalOutputForward(double PercentOutput) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configNominalOutputReverse(double PercentOutput) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configPeakOutputForward(double PercentOutput) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configPeakOutputReverse(double PercentOutput) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configMotionCruiseVelocity(int sensorUnitsPer100ms) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configMotionAcceleration(int sensorunitsPer100msPerSec) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSensorPhase(boolean PhaseSensor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configFactoryDefault() {
		revErrorToException(restoreFactoryDefaults());
	}

	@Override
	public void configOpenLoopRamp(double secondsFromNeutralToFull) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configClosedLoopRamp(double secondsFromNeutralToFull) {
		throw new UnsupportedOperationException();
	}
	
}
