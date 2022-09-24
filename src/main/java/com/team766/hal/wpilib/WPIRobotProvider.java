package com.team766.hal.wpilib;

import com.team766.hal.AnalogInputReader;
import com.team766.hal.CameraInterface;
import com.team766.hal.CameraReader;
import com.team766.hal.Clock;
import com.team766.hal.ControlInputReader;
import com.team766.hal.DigitalInputReader;
import com.team766.hal.EncoderReader;
import com.team766.hal.GyroReader;
import com.team766.hal.JoystickReader;
import com.team766.hal.LocalSpeedController;
import com.team766.hal.PositionReader;
import com.team766.hal.RelayOutput;
import com.team766.hal.RobotProvider;
import com.team766.hal.SolenoidController;
import com.team766.hal.SpeedController;
import com.team766.hal.mock.MockGyro;
import com.team766.hal.mock.MockPositionSensor;
import com.team766.hal.mock.MockSpeedController;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.SPI;

public class WPIRobotProvider extends RobotProvider {

	private SpeedController[][] motors = new SpeedController[SpeedController.Type.values().length][64];

	@Override
	public SpeedController getMotor(int index, SpeedController.Type type, ControlInputReader localSensor) {
		if (motors[type.ordinal()][index] != null) {
			return motors[type.ordinal()][index];
		}
		SpeedController motor = null;
		switch (type) {
			case SparkMax:
				try {
					motor = new CANSparkMaxSpeedController(index);
				} catch (Exception ex) {
					LoggerExceptionUtils.logException(ex);
					motor = new LocalSpeedController(new MockSpeedController(index), localSensor);
					localSensor = null;
				}
				break;
			case TalonSRX:
				motor = new CANTalonSpeedController(index);
				break;
			case VictorSPX:
				motor = new CANVictorSpeedController(index);
				break;
			case TalonFX:
				motor = new CANTalonFxSpeedController(index);
				break;
			case VictorSP:
				motor = new LocalSpeedController(new PWMVictorSP(index), localSensor);
				localSensor = null;
				break;
		}
		if (motor == null) {
			LoggerExceptionUtils.logException(new IllegalArgumentException("Unsupported motor type " + type));
			motor = new LocalSpeedController(new MockSpeedController(index), localSensor);
			localSensor = null;
		}
		if (localSensor != null) {
			motor = new LocalSpeedController(motor, localSensor);
		}
		motors[type.ordinal()][index] = motor;
		return motor;
	}

	@Override
	public EncoderReader getEncoder(int index1, int index2) {
		if(encoders[index1] == null)
			encoders[index1] = new Encoder(index1, index2);
		return encoders[index1];
	}

	@Override
	public SolenoidController getSolenoid(int index) {
		if(solenoids[index] == null)
			solenoids[index] = new Solenoid(index);
		return solenoids[index];
	}

	@Override
	//Gyro index values:
	// -1 	= Spartan Gyro
	// 	0+ 	= Analog Gyro on port index
	public GyroReader getGyro(int index) {
		if(gyros[index + 2] == null){
			if(index < -2) {
				Logger.get(Category.CONFIGURATION).logRaw(
					Severity.ERROR,
					"Invalid gyro port " + index + ". Must be -2, -1, or a non-negative integer"
				);
				return new MockGyro();
			}
			else if(index == -2)
				gyros[index + 2] = new NavXGyro(I2C.Port.kOnboard);
			else if(index == -1)
				gyros[index + 2] = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
			else
				gyros[index + 2] = new AnalogGyro(index);
		}
		return gyros[index + 2];
	}

	@Override
	public CameraReader getCamera(String id, String value) {
		System.err.println("Camera support not yet avaible");
		return null;
	}

	@Override
	public JoystickReader getJoystick(int index) {
		if(joysticks[index] == null)
			joysticks[index] = new Joystick(index);
		return joysticks[index];
	}

	@Override
	public CameraInterface getCamServer(){
		return new com.team766.hal.wpilib.CameraInterface();
	}

	@Override
	public DigitalInputReader getDigitalInput(int index) {
		if (digInputs[index] == null)
			digInputs[index] = new DigitalInput(index);
		return digInputs[index];
	}

	@Override
	public AnalogInputReader getAnalogInput(int index){
		if(angInputs[index] == null)
			angInputs[index] = new AnalogInput(index);
		return angInputs[index];
	}

	@Override
	public RelayOutput getRelay(int index) {
		if(relays[index] == null)
			relays[index] = new Relay(index);
		return relays[index];
	}

	@Override
	public PositionReader getPositionSensor() {
		if (positionSensor == null) {
			positionSensor = new MockPositionSensor();
			Logger.get(Category.CONFIGURATION).logRaw(
				Severity.ERROR,
				"Position sensor does not exist on real robots. Using mock position sensor instead - it will always return a position of 0"
			);
		}
		return positionSensor;
	}

	@Override
	public Clock getClock() {
		return SystemClock.instance;
	}

	@Override
	public boolean hasNewDriverStationData() {
		return DriverStation.isNewControlData();
	}

	@Override
	public double getBatteryVoltage() {
		return RobotController.getBatteryVoltage();
	}
}