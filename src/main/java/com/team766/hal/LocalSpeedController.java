package com.team766.hal;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team766.controllers.PIDController;
import com.team766.framework.Scheduler;
import com.team766.library.ConstantValueProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;

public class LocalSpeedController implements SpeedController {
	private BasicSpeedController motor;
	private ControlInputReader sensor;
	private PIDController pidController;

	private ConstantValueProvider<Double> pGain = new ConstantValueProvider<Double>(0.0);
	private ConstantValueProvider<Double> iGain = new ConstantValueProvider<Double>(0.0);
	private ConstantValueProvider<Double> dGain = new ConstantValueProvider<Double>(0.0);
	private ConstantValueProvider<Double> ffGain = new ConstantValueProvider<Double>(0.0);
	private ConstantValueProvider<Double> outputMaxLow = new ConstantValueProvider<Double>(-1.0);
	private ConstantValueProvider<Double> outputMaxHigh = new ConstantValueProvider<Double>(1.0);

	private boolean inverted = false;
	private boolean sensorInverted = false;
	private double sensorOffset = 0.0;

	private ControlMode controlMode = ControlMode.PercentOutput;
	private double setpoint = 0.0;
	private SpeedController leader = null;

	public LocalSpeedController(BasicSpeedController motor, ControlInputReader sensor){
		this.motor = motor;
		this.sensor = sensor;
		this.pidController = new PIDController(
			pGain, iGain, dGain, ffGain, outputMaxLow, outputMaxHigh,
			new ConstantValueProvider<Double>(0.0));

		Scheduler.getInstance().add(new Runnable() {
			@Override
			public void run() {
				switch (LocalSpeedController.this.controlMode) {
					case Current:
						LoggerExceptionUtils.logException(new UnsupportedOperationException(toString() + " does not support Current control mode"));
						stopMotor();
						break;
					case Disabled:
						// support proper output disabling if this.motor is a SpeedController
						if (LocalSpeedController.this.motor instanceof SpeedController) {
							((SpeedController)LocalSpeedController.this.motor).set(ControlMode.Disabled, 0);
						} else {
							setPower(0);
						}
						break;
					case Follower:
						setPower(leader.get());
						break;
					case MotionMagic:
						LoggerExceptionUtils.logException(new UnsupportedOperationException(toString() + " does not support MotionMagic control mode"));
						stopMotor();
						break;
					case MotionProfile:
						LoggerExceptionUtils.logException(new UnsupportedOperationException(toString() + " does not support MotionProfile control mode"));
						stopMotor();
						break;
					case MotionProfileArc:
						LoggerExceptionUtils.logException(new UnsupportedOperationException(toString() + " does not support MotionProfileArc control mode"));
						stopMotor();
						break;
					case PercentOutput:
						setPower(setpoint);
						break;
					case Position:
						pidController.calculate(getSensorPosition(), false);
						setPower(pidController.getOutput());
						break;
					case Velocity:
						pidController.calculate(getSensorVelocity(), false);
						setPower(pidController.getOutput());
						break;
					case Voltage:
						setPower(setpoint / RobotProvider.instance.getBatteryVoltage());
						break;
				}
			}

			@Override
			public String toString() {
				return LocalSpeedController.this.toString();
			}
		});
	}

	@Override
	public String toString() {
		return "LocalSpeedController:" + LocalSpeedController.this.motor.toString();
	}

	private void setPower(double power) {
		if (this.inverted) {
			power *= -1;
		}
		this.motor.set(power);
	}
	
	@Override
	public double get() {
		double value = motor.get();
		if (this.inverted) {
			value *= -1;
		}
		return value;
	}
	
	@Override
	public void set(double speed) {
		set(ControlMode.PercentOutput, speed);
	}

	@Override
	public void setInverted(boolean isInverted) {
		this.inverted = isInverted;
	}

	@Override
	public boolean getInverted() {
		return this.inverted;
	}

	@Override
	public void stopMotor() {
		set(ControlMode.PercentOutput, 0);
	}

	@Override
	public void setSensorPosition(double position) {
		if (this.sensor == null) {
			Logger.get(Category.CONFIGURATION).logRaw(Severity.ERROR, toString() + " does not have an attached sensor configured");
			return;
		}
		if (this.sensorInverted) {
			position *= -1;
		}
		sensorOffset = position - sensor.getPosition();
	}
	
	@Override
	public double getSensorPosition() {
		if (this.sensor == null) {
			Logger.get(Category.CONFIGURATION).logRaw(Severity.ERROR, toString() + " does not have an attached sensor configured");
			return 0.0;
		}
		double position = sensor.getPosition() + sensorOffset;
		if (this.sensorInverted) {
			position *= -1;
		}
		return position;
	}

	@Override
	public double getSensorVelocity() {
		if (this.sensor == null) {
			Logger.get(Category.CONFIGURATION).logRaw(Severity.ERROR, toString() + " does not have an attached sensor configured");
			return 0.0;
		}
		double velocity = sensor.getRate();
		if (this.sensorInverted) {
			velocity *= -1;
		}
		return velocity;
	}

	@Override
	public void set(ControlMode mode, double value) {
		if (mode == ControlMode.Follower) {
			throw new IllegalArgumentException("Use follow() method instead of passing Follower to set()");
		}
		if (this.controlMode != mode) {
			pidController.reset();
		}
		this.controlMode = mode;
		this.setpoint = value;
	}
	
	public ControlMode getControlMode() {
		return this.controlMode;
	}

	@Override
	public void follow(SpeedController leader) {
		if (leader == null) {
			throw new IllegalArgumentException("leader argument to follow() is null");
		}
		// TODO: detect if this.motor is a SpeedController, and delegate to its follow() method if so.
		this.controlMode = ControlMode.Follower;
		this.leader = leader;
	}
	
	@Override
	public void setNeutralMode(NeutralMode neutralMode) {
		if (this.motor instanceof SpeedController) {
			((SpeedController)this.motor).setNeutralMode(neutralMode);
		} else {
			LoggerExceptionUtils.logException(new UnsupportedOperationException(this.toString() + " - setNeutralMode() is only unsupported with CAN speed controllers"));
		}
	}

	@Override
	public void setP(double value) {
		pGain.set(value);
	}

	@Override
	public void setI(double value) {
		iGain.set(value);
	}

	@Override
	public void setD(double value) {
		dGain.set(value);
	}

	@Override
	public void setFF(double value) {
		ffGain.set(value);
	}

	@Override
	public void setSelectedFeedbackSensor(FeedbackDevice feedbackDevice) {
		LoggerExceptionUtils.logException(new UnsupportedOperationException("setSelectedFeedbsckSensor() is currently unsupported by LocalSpeedController"));
	}

	@Override
	public void setSensorInverted(boolean inverted) {
		this.sensorInverted = inverted;
	}

	@Override
	public void setOutputRange(double minOutput, double maxOutput) {
		outputMaxLow.set(minOutput);
		outputMaxHigh.set(maxOutput);
	}

	@Override
	public void setCurrentLimit(double ampsLimit) {
		LoggerExceptionUtils.logException(new UnsupportedOperationException("setCurrentLimit() is currently unsupported by LocalSpeedController"));
	}

	@Override
	public void restoreFactoryDefault() {
		this.motor.restoreFactoryDefault();

		this.pGain.set(0.0);
		this.iGain.set(0.0);
		this.dGain.set(0.0);
		this.ffGain.set(0.0);
		this.outputMaxLow.set(-1.0);
		this.outputMaxHigh.set(1.0);

		this.inverted = false;
		this.sensorInverted = false;
		this.controlMode = ControlMode.Disabled;
		this.setpoint = 0.0;
	}

	@Override
	public void setOpenLoopRamp(double secondsFromNeutralToFull) {
		LoggerExceptionUtils.logException(new UnsupportedOperationException("setOpenLoopRamp() is currently unsupported by LocalSpeedController"));
	}

	@Override
	public void setClosedLoopRamp(double secondsFromNeutralToFull) {
		LoggerExceptionUtils.logException(new UnsupportedOperationException("setClosedLoopRamp() is currently unsupported by LocalSpeedController"));
	}
}