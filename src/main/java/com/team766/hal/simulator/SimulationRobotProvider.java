package com.team766.hal.simulator;

import com.team766.hal.AnalogInputReader;
import com.team766.hal.CameraInterface;
import com.team766.hal.CameraReader;
import com.team766.hal.Clock;
import com.team766.hal.ControlInputReader;
import com.team766.hal.DigitalInputReader;
import com.team766.hal.EncoderReader;
import com.team766.hal.GyroReader;
import com.team766.hal.JoystickReader;
import com.team766.hal.LocalMotorController;
import com.team766.hal.PositionReader;
import com.team766.hal.RelayOutput;
import com.team766.hal.RobotProvider;
import com.team766.hal.SolenoidController;
import com.team766.hal.MotorController;
import com.team766.simulator.ProgramInterface;

public class SimulationRobotProvider extends RobotProvider{

	private MotorController[] motors = new MotorController[64];
	private int m_dsUpdateNumber = 0;

	@Override
	public MotorController getMotor(int index, MotorController.Type type, ControlInputReader localSensor) {
		if(motors[index] == null) {
			motors[index] = new SimMotorController(index);
			if (localSensor != null) {
				motors[index] = new LocalMotorController(motors[index], localSensor);
			}
		}
		return motors[index];
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
	public GyroReader getGyro(int index) {
		if(gyros[0] == null)
			gyros[0] = new Gyro();
		return gyros[0];
	}

	@Override
	public CameraReader getCamera(String id, String value) {
		if(!cams.containsKey(id))
			cams.put(id, new Camera());
		return cams.get(id);
	}

	@Override
	public JoystickReader getJoystick(int index) {
		return ProgramInterface.joystickChannels[index];
	}
	
	@Override
	public DigitalInputReader getDigitalInput(int index) {
		if(digInputs[index] == null)
			digInputs[index] = new DigitalInput(index);
		return digInputs[index];
	}

	@Override
	public CameraInterface getCamServer() {
		return null;
	}
	
	@Override
	public AnalogInputReader getAnalogInput(int index) {
		if(angInputs[index] == null)
			angInputs[index] = new AnalogInput(index);
		return angInputs[index];
	}
	
	public RelayOutput getRelay(int index) {
		if(relays[index] == null)
			relays[index] = new Relay(index);
		return relays[index];
	}

	@Override
	public PositionReader getPositionSensor() {
		if (positionSensor == null)
			positionSensor = new PositionSensor();
		return positionSensor;
	}

	@Override
	public Clock getClock() {
		return SimulationClock.instance;
	}

	@Override
	public boolean hasNewDriverStationData() {
		int newUpdateNumber = ProgramInterface.driverStationUpdateNumber;
		boolean result = m_dsUpdateNumber != newUpdateNumber;
		m_dsUpdateNumber = newUpdateNumber;
		return result;
	}

	@Override
	public double getBatteryVoltage() {
		// TODO: The simulator currently doesn't include a simulation of electrical load.
		return 12.0;
	}
}
