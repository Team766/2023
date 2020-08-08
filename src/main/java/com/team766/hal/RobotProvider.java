package com.team766.hal;

import java.util.HashMap;

import com.team766.config.ConfigFileReader;

public abstract class RobotProvider {
	
	public static RobotProvider instance;
	
	protected SpeedController[] motors = new SpeedController[12];
	protected CANSpeedController[] canMotors = new CANSpeedController[64];
	protected EncoderReader[] encoders = new EncoderReader[20];
	protected SolenoidController[] solenoids = new SolenoidController[10];
	protected GyroReader[] gyros = new GyroReader[13];
	protected HashMap<String, CameraReader> cams = new HashMap<String, CameraReader>();
	protected JoystickReader[] joysticks = new JoystickReader[3];
	protected DigitalInputReader[] digInputs = new DigitalInputReader[8];
	protected AnalogInputReader[] angInputs = new AnalogInputReader[5];
	protected RelayOutput[] relays = new RelayOutput[5];
	
	//HAL
	public abstract SpeedController getMotor(int index);
	public abstract CANSpeedController getCANMotor(int index);
	
	public abstract EncoderReader getEncoder(int index1, int index2);
	
	public abstract DigitalInputReader getDigitalInput(int index);
	
	public abstract AnalogInputReader getAnalogInput(int index);
	
	public abstract RelayOutput getRelay(int index);
	
	public abstract SolenoidController getSolenoid(int index);
	
	public abstract GyroReader getGyro(int index);
	
	public abstract CameraReader getCamera(String id, String value);
	
	//Config-driven methods
	public SpeedController getMotor(String configName) {
		Integer port = ConfigFileReader.getInstance().getInt(configName).get();
		if (port == null) {
			throw new IllegalArgumentException("Motor " + configName + " not found in config file");
		}
		return getMotor(port);
	}
	public CANSpeedController getCANMotor(String configName) {
		Integer port = ConfigFileReader.getInstance().getInt(configName).get();
		if (port == null) {
			throw new IllegalArgumentException("CAN Motor " + configName + " not found in config file");
		}
		return getCANMotor(port);
	}
	public EncoderReader getEncoder(String configName) {
		Integer[] ports = ConfigFileReader.getInstance().getInts(configName).get();
		if (ports == null) {
			throw new IllegalArgumentException("Encoder " + configName + " not found in config file");
		}
		if (ports.length != 2) {
			throw new IllegalArgumentException("Encoder " + configName + " has " + ports.length + " config values, but expected 2");
		}
		return getEncoder(ports[0], ports[1]);
	}
	public DigitalInputReader getDigitalInput(String configName) {
		Integer port = ConfigFileReader.getInstance().getInt(configName).get();
		if (port == null) {
			throw new IllegalArgumentException("Digital input " + configName + " not found in config file");
		}
		return getDigitalInput(port);
	}
	public AnalogInputReader getAnalogInput(String configName) {
		Integer port = ConfigFileReader.getInstance().getInt(configName).get();
		if (port == null) {
			throw new IllegalArgumentException("Analog input " + configName + " not found in config file");
		}
		return getAnalogInput(port);
	}
	public RelayOutput getRelay(String configName) {
		Integer port = ConfigFileReader.getInstance().getInt(configName).get();
		if (port == null) {
			throw new IllegalArgumentException("Relay " + configName + " not found in config file");
		}
		return getRelay(port);
	}
	public SolenoidController getSolenoid(String configName) {
		Integer port = ConfigFileReader.getInstance().getInt(configName).get();
		if (port == null) {
			throw new IllegalArgumentException("Solenoid " + configName + " not found in config file");
		}
		return getSolenoid(port);
	}
	public GyroReader getGyro(String configName) {
		Integer port = ConfigFileReader.getInstance().getInt(configName).get();
		if (port == null) {
			throw new IllegalArgumentException("Gyro " + configName + " not found in config file");
		}
		return getGyro(port);
	}
	
	//Operator Devices
	public abstract JoystickReader getJoystick(int index);
	
	public abstract CameraInterface getCamServer();
	
	public abstract Clock getClock();
}
