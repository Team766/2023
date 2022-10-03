package com.team766.hal;

import java.util.Arrays;
import java.util.HashMap;

import com.team766.config.ConfigFileReader;
import com.team766.controllers.TimeProviderI;
import com.team766.hal.mock.MockAnalogInput;
import com.team766.hal.mock.MockDigitalInput;
import com.team766.hal.mock.MockEncoder;
import com.team766.hal.mock.MockGyro;
import com.team766.hal.mock.MockRelay;
import com.team766.hal.mock.MockMotorController;
import com.team766.library.ValueProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

public abstract class RobotProvider {
	
	public static RobotProvider instance;
	
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
	public abstract MotorController getMotor(int index, MotorController.Type type, ControlInputReader localSensor);
	
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
	public MotorController getMotor(String configName) {
		final String encoderConfigName = configName + ".encoder";
		final String analogInputConfigName = configName + ".analogInput";
		final ControlInputReader sensor =
			ConfigFileReader.getInstance().containsKey(encoderConfigName) ? getEncoder(encoderConfigName) :
			ConfigFileReader.getInstance().containsKey(analogInputConfigName) ? getAnalogInput(analogInputConfigName) :
			null;

		try {
			ValueProvider<Integer> deviceId = ConfigFileReader.getInstance().getInt(configName + ".deviceId");
			final ValueProvider<Integer> port = ConfigFileReader.getInstance().getInt(configName + ".port");
			final ValueProvider<Double> sensorScaleConfig = ConfigFileReader.getInstance().getDouble(configName + ".sensorScale");
			final ValueProvider<Boolean> invertedConfig = ConfigFileReader.getInstance().getBoolean(configName + ".inverted");
			final ValueProvider<Boolean> sensorInvertedConfig = ConfigFileReader.getInstance().getBoolean(configName + ".sensorInverted");
			final ValueProvider<MotorController.Type> type = ConfigFileReader.getInstance().getEnum(MotorController.Type.class, configName + ".type");

			if (deviceId.hasValue() && port.hasValue()) {
				Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Motor %s configuration should have only one of `deviceId` or `port`", configName);
			}

			MotorController.Type defaultType = MotorController.Type.TalonSRX;
			if (!deviceId.hasValue()) {
				deviceId = port;
				defaultType = MotorController.Type.VictorSP;
			}

			var motor = getMotor(deviceId.get(), type.valueOr(defaultType), sensor);
			if (sensorScaleConfig.hasValue()) {
				motor = new MotorControllerWithSensorScale(motor, sensorScaleConfig.get());
			}
			if (invertedConfig.valueOr(false)) {
				motor.setInverted(true);
			}
			if (sensorInvertedConfig.valueOr(false)) {
				motor.setSensorInverted(true);
			}
			return motor;
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Motor %s not found in config file, using mock motor instead", configName);
			return new LocalMotorController(new MockMotorController(0), sensor);
		}
	}
	public EncoderReader getEncoder(String configName) {
		try {
			final ValueProvider<Integer[]> ports = ConfigFileReader.getInstance().getInts(configName + ".ports");
			final ValueProvider<Double> distancePerPulseConfig = ConfigFileReader.getInstance().getDouble(configName + ".distancePerPulse");

			final var portsValue = ports.get();
			if (portsValue.length != 2) {
				Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Encoder %s has %d config values, but expected 2", configName, portsValue.length);
				return new MockEncoder(0, 0);
			}
			final EncoderReader reader = getEncoder(portsValue[0], portsValue[1]);
			if (distancePerPulseConfig.hasValue()) {
				reader.setDistancePerPulse(distancePerPulseConfig.get());
			}
			return reader;
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Encoder %s not found in config file, using mock encoder instead", configName);
			return new MockEncoder(0, 0);
		}
	}
	public DigitalInputReader getDigitalInput(String configName) {
		try {
			ValueProvider<Integer> port = ConfigFileReader.getInstance().getInt(configName + ".port");

			return getDigitalInput(port.get());
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Digital input %s not found in config file, using mock digital input instead", configName);
			return new MockDigitalInput();
		}
	}
	public AnalogInputReader getAnalogInput(String configName) {
		try {
			ValueProvider<Integer> port = ConfigFileReader.getInstance().getInt(configName + ".port");

			return getAnalogInput(port.get());
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Analog input %s not found in config file, using mock analog input instead", configName);
			return new MockAnalogInput();
		}
	}
	public RelayOutput getRelay(String configName) {
		try {
			ValueProvider<Integer> port = ConfigFileReader.getInstance().getInt(configName + ".port");

			return getRelay(port.get());
		} catch (IllegalArgumentException ex) {
			Logger.get(Category.CONFIGURATION).logData(Severity.ERROR, "Relay %s not found in config file, using mock relay instead", configName);
			return new MockRelay(0);
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
			return new MockGyro();
		}
	}

	//Operator Devices
	public abstract JoystickReader getJoystick(int index);
	
	public abstract CameraInterface getCamServer();
	
	public abstract Clock getClock();

	public abstract double getBatteryVoltage();

	public abstract boolean hasNewDriverStationData();
}
