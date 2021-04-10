package com.team766.hal.wpilib;

import com.team766.hal.AnalogInputReader;
import com.team766.hal.CANSpeedController;
import com.team766.hal.CameraInterface;
import com.team766.hal.CameraReader;
import com.team766.hal.Clock;
import com.team766.hal.DigitalInputReader;
import com.team766.hal.EncoderReader;
import com.team766.hal.GyroReader;
import com.team766.hal.JoystickReader;
import com.team766.hal.PositionReader;
import com.team766.hal.RelayOutput;
import com.team766.hal.RobotProvider;
import com.team766.hal.SolenoidController;
import com.team766.hal.SpeedController;
import com.team766.hal.mock.PositionSensor;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;

public class WPIRobotProvider extends RobotProvider {

	@Override
	public SpeedController getMotor(int index) {
		if(motors[index] == null){
			motors[index] = new Victor(index);
		}

		return motors[index];
	}

	@Override
	public CANSpeedController getTalonCANMotor(int index) {
		if (talonCanMotors[index] == null) {
			talonCanMotors[index] = new CANTalonSpeedController(index);
		}

		return talonCanMotors[index];
	}

	@Override
	public CANSpeedController getVictorCANMotor(int index) {
		if (victorCanMotors[index] == null) {
			victorCanMotors[index] = new CANVictorSpeedController(index);
		}

		return victorCanMotors[index];
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
		if(gyros[index + 1] == null){
			if(index == -1)
				gyros[index + 1] = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
			else
				gyros[index + 1] = new AnalogGyro(index);
		}
		return gyros[index + 1];
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
			positionSensor = new PositionSensor();
			Logger.get(Category.CONFIGURATION).logData(
				Severity.ERROR,
				"Position sensor does not exist on real robots. Using mock position sensor instead - it will always return a position of 0"
			);
		}
		return null;
	}

	@Override
	public Clock getClock() {
		return SystemClock.instance;
	}

	@Override
	public boolean hasNewDriverStationData() {
		return DriverStation.getInstance().isNewControlData();
	}
}