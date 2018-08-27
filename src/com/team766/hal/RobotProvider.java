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
		return getMotor(ConfigFileReader.getInstance().getInt(configName));
	}
	public CANSpeedController getCANMotor(String configName) {
		return getCANMotor(ConfigFileReader.getInstance().getInt(configName));
	}
	public EncoderReader getEncoder(String configName) {
		int[] ports = ConfigFileReader.getInstance().getInts(configName);
		if (ports.length != 2) {
			throw new IllegalArgumentException("Encoder " + configName + " has " + ports.length + " config values, but expected 2");
		}
		return getEncoder(ports[0], ports[1]);
	}
	public DigitalInputReader getDigitalInput(String configName) {
		return getDigitalInput(ConfigFileReader.getInstance().getInt(configName));
	}
	public AnalogInputReader getAnalogInput(String configName) {
		return getAnalogInput(ConfigFileReader.getInstance().getInt(configName));
	}
	public RelayOutput getRelay(String configName) {
		return getRelay(ConfigFileReader.getInstance().getInt(configName));
	}
	public SolenoidController getSolenoid(String configName) {
		return getSolenoid(ConfigFileReader.getInstance().getInt(configName));
	}
	public GyroReader getGyro(String configName) {
		return getGyro(ConfigFileReader.getInstance().getInt(configName));
	}
	
	//Operator Devices
	public abstract JoystickReader getJoystick(int index);
	
	public abstract CameraInterface getCamServer();
}
