package com.team766.hal;

import java.util.Arrays;
import java.util.HashMap;

import com.team766.config.ConfigFileReader;
import com.team766.controllers.TimeProviderI;
import com.team766.hal.mock.*;
import com.team766.library.ValueProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

public abstract class RobotProvider {
	
	public static RobotProvider instance;
	
	protected SpeedController[] motors = new SpeedController[12];
	protected EncoderReader[] encoders = new EncoderReader[20];
	protected SolenoidController[] solenoids = new SolenoidController[10];
	protected GyroReader[] gyros = new GyroReader[13];
	protected HashMap<String, CameraReader> cams = new HashMap<String, CameraReader>();
	protected JoystickReader[] joysticks = new JoystickReader[8];
	protected DigitalInputReader[] digInputs = new DigitalInputReader[8];
	protected AnalogInputReader[] angInputs = new AnalogInputReader[5];
	protected RelayOutput[] relays = new RelayOutput[5];
	protected PositionReader positionSensor = null;
	
	//HAL
	public abstract SpeedController getMotor(int index);
	public abstract CANSpeedController getCANMotor(int index, CANSpeedController.Type type);
	
	public abstract EncoderReader getEncoder(int index1, int index2);
	
	public abstract DigitalInputReader getDigitalInput(int index);
	
	public abstract AnalogInputReader getAnalogInput(int index);
	
	public abstract RelayOutput getRelay(int index);
	
	public abstract SolenoidController getSolenoid(int index);
	
	public abstract GyroReader getGyro(int index);
	
	public abstract CameraReader getCamera(String id, String value);

	public abstract PositionReader getPositionSensor();

	public static TimeProviderI getTimeProvider(){
		return () -> instance.getClock().getTime();
	}
	
	// Config-driven methods
	public SpeedController getMotor(String configName) {
		try {
			ValueProvider<Integer> port = ConfigFileReader.getInstance().getInt(configName + ".port");
			ValueProvider<Boolean> invertedConfig = ConfigFileReader.getInstance().getBoolean(configName + ".inverted");

			var motor = getMotor(port.get());
			if (invertedConfig.valueOr(false)) {
				motor.setInverted(true);
			}
			return motor;
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Motor %s not found in config file, using mock motor instead", configName);
			return new Victor(0);
		}
	}
	public CANSpeedController getCANMotor(String configName) {
		try {
			ValueProvider<Integer> port = ConfigFileReader.getInstance().getInt(configName + ".deviceId");
			ValueProvider<Double> sensorScaleConfig = ConfigFileReader.getInstance().getDouble(configName + ".sensorScale");
			ValueProvider<Boolean> invertedConfig = ConfigFileReader.getInstance().getBoolean(configName + ".inverted");
			ValueProvider<Boolean> sensorInvertedConfig = ConfigFileReader.getInstance().getBoolean(configName + ".sensorInverted");
			ValueProvider<CANSpeedController.Type> type = ConfigFileReader.getInstance().getEnum(CANSpeedController.Type.class, configName + ".type");

			var motor = getCANMotor(port.get(), type.valueOr(CANSpeedController.Type.TalonSRX));
			if (sensorScaleConfig.hasValue()) {
				motor = new CANSpeedControllerWithSensorScale(motor, sensorScaleConfig.get());
			}
			if (invertedConfig.valueOr(false)) {
				motor.setInverted(true);
			}
			if (sensorInvertedConfig.valueOr(false)) {
				motor.setSensorInverted(true);
			}
			return motor;
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "CAN Motor %s not found in config file, using mock motor instead", configName);
			return new Talon(0);
		}
	}
	public EncoderReader getEncoder(String configName) {
		try {
			ValueProvider<Integer[]> ports = ConfigFileReader.getInstance().getInts(configName + ".ports");
			ValueProvider<Double> distancePerPulseConfig = ConfigFileReader.getInstance().getDouble(configName + ".distancePerPulse");

			var portsValue = ports.get();
			if (portsValue.length != 2) {
				Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Encoder %s has %d config values, but expected 2", configName, portsValue.length);
				return new Encoder(0, 0);
			}
			EncoderReader reader = getEncoder(portsValue[0], portsValue[1]);
			if (distancePerPulseConfig.hasValue()) {
				reader.setDistancePerPulse(distancePerPulseConfig.get());
			}
			return reader;
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Encoder %s not found in config file, using mock encoder instead", configName);
			return new Encoder(0, 0);
		}
	}
	public DigitalInputReader getDigitalInput(String configName) {
		try {
			ValueProvider<Integer> port = ConfigFileReader.getInstance().getInt(configName + ".port");

			return getDigitalInput(port.get());
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Digital input %s not found in config file, using mock digital input instead", configName);
			return new DigitalInput();
		}
	}
	public AnalogInputReader getAnalogInput(String configName) {
		try {
			ValueProvider<Integer> port = ConfigFileReader.getInstance().getInt(configName + ".port");

			return getAnalogInput(port.get());
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Analog input %s not found in config file, using mock analog input instead", configName);
			return new AnalogInput();
		}
	}
	public RelayOutput getRelay(String configName) {
		try {
			ValueProvider<Integer> port = ConfigFileReader.getInstance().getInt(configName + ".port");

			return getRelay(port.get());
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Relay %s not found in config file, using mock relay instead", configName);
			return new Relay(0);
		}
	}
	public DoubleSolenoid getSolenoid(String configName) {
		try {
			final String legacyConfigKey = configName + ".port";
			ValueProvider<Integer[]> forwardPorts =
				ConfigFileReader.getInstance().containsKey(legacyConfigKey)
					? ConfigFileReader.getInstance().getInts(legacyConfigKey)
					: ConfigFileReader.getInstance().getInts(configName + ".forwardPort");
			ValueProvider<Integer[]> reversePorts =
				ConfigFileReader.getInstance().getInts(configName + ".reversePort");

			SolenoidController forwardSolenoids = new MultiSolenoid(
				Arrays.stream(forwardPorts.valueOr(new Integer[0]))
					.<SolenoidController>map(this::getSolenoid)
					.toArray(SolenoidController[]::new));
			SolenoidController reverseSolenoids = new MultiSolenoid(
				Arrays.stream(reversePorts.valueOr(new Integer[0]))
					.<SolenoidController>map(this::getSolenoid)
					.toArray(SolenoidController[]::new));
			return new DoubleSolenoid(forwardSolenoids, reverseSolenoids);
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Solenoid %s not found in config file, using mock solenoid instead", configName);
			return new DoubleSolenoid(null, null);
		}
	}
	public GyroReader getGyro(String configName) {
		try {
			ValueProvider<Integer> port = ConfigFileReader.getInstance().getInt(configName + ".port");

			return getGyro(port.get());
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Gyro %s not found in config file, using mock gyro instead", configName);
			return new Gyro();
		}
	}

	//Operator Devices
	public abstract JoystickReader getJoystick(int index);
	
	public abstract CameraInterface getCamServer();
	
	public abstract Clock getClock();

	public abstract boolean hasNewDriverStationData();
}
