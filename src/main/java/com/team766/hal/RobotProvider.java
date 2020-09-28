package com.team766.hal;

import java.util.HashMap;

import com.team766.config.ConfigFileReader;
import com.team766.controllers.TimeProviderI;
import com.team766.hal.mock.*;

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

	public static TimeProviderI getTimeProvider(){
		return () -> instance.getClock().getTime();
	}
	
	// Config-driven methods
	public SpeedController getMotor(String configName) {
		try {
			Integer port = ConfigFileReader.getInstance().getInt(configName).get();
			return getMotor(port);
		} catch (IllegalArgumentException ex) {
			System.out.println("Motor " + configName + " not found in config file, using mock motor instead");
			return new Victor(0);
		}
	}
	public CANSpeedController getTalonCANMotor(String configName) {
		try {
			Integer port = ConfigFileReader.getInstance().getInt(configName).get();
			return getTalonCANMotor(port);
		} catch (IllegalArgumentException ex) {
			System.out.println("Talon CAN Motor " + configName + " not found in config file, using mock talon instead");
			return new Talon(0);
		}
	}
	public CANSpeedController getVictorCANMotor(String configName) {
		try {
			Integer port = ConfigFileReader.getInstance().getInt(configName).get();
			return getVictorCANMotor(port);
		} catch (IllegalArgumentException ex) {
			System.out.println("Victor CAN Motor " + configName + " not found in config file, using mock victor instead");
			return new Talon(0);
		}
	}
	public EncoderReader getEncoder(String configName) {
		try {
			Integer[] ports = ConfigFileReader.getInstance().getInts(configName).get();
			if (ports.length != 2) {
				System.out.println("Encoder " + configName + " has " + ports.length + " config values, but expected 2");
				return new Encoder(0, 0);
			}
			return getEncoder(ports[0], ports[1]);
		} catch (IllegalArgumentException ex) {
			System.out.println("Encoder " + configName + " not found in config file, using mock encoder instead");
			return new Encoder(0, 0);
		}
	}
	public DigitalInputReader getDigitalInput(String configName) {
		try {
			Integer port = ConfigFileReader.getInstance().getInt(configName).get();
			return getDigitalInput(port);
		} catch (IllegalArgumentException ex) {
			System.out.println("Digital input " + configName + " not found in config file, using mock digital input");
			return new DigitalInput();
		}
	}
	public AnalogInputReader getAnalogInput(String configName) {
		try {
			Integer port = ConfigFileReader.getInstance().getInt(configName).get();
			return getAnalogInput(port);
		} catch (IllegalArgumentException ex) {
			System.out.println("Analog input " + configName + " not found in config file, using mock analog input instead");
			return new AnalogInput();
		}
	}
	public RelayOutput getRelay(String configName) {
		try {
			Integer port = ConfigFileReader.getInstance().getInt(configName).get();
			return getRelay(port);
		} catch (IllegalArgumentException ex) {
			System.out.println("Relay " + configName + " not found in config file, using mock relay instead");
			return new Relay(0);
		}
	}
	public SolenoidController getSolenoid(String configName) {
		try {
			Integer port = ConfigFileReader.getInstance().getInt(configName).get();
			return getSolenoid(port);
		} catch (IllegalArgumentException ex) {
			System.out.println("Solenoid " + configName + " not found in config file, using mock solenoid instead");
			return new Solenoid(0);
		}
	}
	public GyroReader getGyro(String configName) {
		try {
			Integer port = ConfigFileReader.getInstance().getInt(configName).get();
			return getGyro(port);
		} catch (IllegalArgumentException ex) {
			System.out.println("Gyro " + configName + " not found in config file, using mock gyro instead");
			return new Gyro();
		}
	}

	//Operator Devices
	public abstract JoystickReader getJoystick(int index);
	
	public abstract CameraInterface getCamServer();
	
	public abstract Clock getClock();

	public abstract boolean hasNewDriverStationData();
}
