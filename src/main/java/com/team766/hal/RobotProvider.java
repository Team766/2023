package com.team766.hal;

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
	protected CANSpeedController[] talonCanMotors = new CANSpeedController[64];
	protected CANSpeedController[] victorCanMotors = new CANSpeedController[64];
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
	public abstract CANSpeedController getTalonCANMotor(int index);
	public abstract CANSpeedController getVictorCANMotor(int index);
	
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
			Integer port = ConfigFileReader.getInstance().getInt(configName + ".port").get();
			return getMotor(port);
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Motor %s not found in config file, using mock motor instead", configName);
			return new Victor(0);
		}
	}
	public CANSpeedController getTalonCANMotor(String configName) {
		try {
			Integer port = ConfigFileReader.getInstance().getInt(configName + ".deviceId").get();
			return getTalonCANMotor(port);
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Talon CAN Motor %s not found in config file, using mock talon instead", configName);
			return new Talon(0);
		}
	}
	public CANSpeedController getVictorCANMotor(String configName) {
		try {
			Integer port = ConfigFileReader.getInstance().getInt(configName + ".deviceId").get();
			return getVictorCANMotor(port);
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Victor CAN Motor %s not found in config file, using mock victor instead", configName);
			return new Talon(0);
		}
	}
	public EncoderReader getEncoder(String configName) {
		try {
			Integer[] ports = ConfigFileReader.getInstance().getInts(configName + ".ports").get();
			if (ports.length != 2) {
				Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Encoder %s has %d config values, but expected 2", configName, ports.length);
				return new Encoder(0, 0);
			}
			EncoderReader reader = getEncoder(ports[0], ports[1]);
			ValueProvider<Double> distancePerPulseConfig = ConfigFileReader.getInstance().getDouble(configName + ".distancePerPulse");
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
			Integer port = ConfigFileReader.getInstance().getInt(configName + ".port").get();
			return getDigitalInput(port);
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Digital input %s not found in config file, using mock digital input instead", configName);
			return new DigitalInput();
		}
	}
	public AnalogInputReader getAnalogInput(String configName) {
		try {
			Integer port = ConfigFileReader.getInstance().getInt(configName + ".port").get();
			return getAnalogInput(port);
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Analog input %s not found in config file, using mock analog input instead", configName);
			return new AnalogInput();
		}
	}
	public RelayOutput getRelay(String configName) {
		try {
			Integer port = ConfigFileReader.getInstance().getInt(configName + ".port").get();
			return getRelay(port);
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Relay %s not found in config file, using mock relay instead", configName);
			return new Relay(0);
		}
	}
	public SolenoidController getSolenoid(String configName) {
		try {
			Integer port = ConfigFileReader.getInstance().getInt(configName + ".port").get();
			return getSolenoid(port);
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Solenoid %s not found in config file, using mock solenoid instead", configName);
			return new Solenoid(0);
		}
	}
	public GyroReader getGyro(String configName) {
		try {
			Integer port = ConfigFileReader.getInstance().getInt(configName + ".port").get();
			return getGyro(port);
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
