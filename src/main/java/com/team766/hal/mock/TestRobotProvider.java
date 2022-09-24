package com.team766.hal.mock;

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
import com.team766.hal.wpilib.SystemClock;

public class TestRobotProvider extends RobotProvider{

	private SpeedController[] motors = new SpeedController[64];
	private boolean m_hasDriverStationUpdate = false;
	private double m_batteryVoltage = 12.0;

	@Override
	public SpeedController getMotor(int index, SpeedController.Type type, ControlInputReader localSensor) {
		if(motors[index] == null) {
			motors[index] = new LocalSpeedController(
				new MockSpeedController(index),
				localSensor != null ? localSensor : new MockEncoder(-1, -1));
		}
		return motors[index];
	}

	@Override
	public EncoderReader getEncoder(int index1, int index2) {
		if(encoders[index1] == null)
			encoders[index1] = new MockEncoder(index1, index2);
		return encoders[index1];
	}

	@Override
	public SolenoidController getSolenoid(int index) {
		if(solenoids[index] == null)
			solenoids[index] = new MockSolenoid(index);
		return solenoids[index];
	}

	@Override
	public GyroReader getGyro(int index) {
		if(gyros[0] == null)
			gyros[0] = new MockGyro();
		return gyros[0];
	}

	@Override
	public CameraReader getCamera(String id, String value) {
		if(!cams.containsKey(id))
			cams.put(id, new MockCamera());
		return cams.get(id);
	}

	@Override
	public JoystickReader getJoystick(int index) {
		if(joysticks[index] == null)
			joysticks[index] = new MockJoystick();
		return joysticks[index];
	}
	
	@Override
	public DigitalInputReader getDigitalInput(int index) {
		if(digInputs[index] == null)
			digInputs[index] = new MockDigitalInput();
		return digInputs[index];
	}

	@Override
	public CameraInterface getCamServer() {
		return null;
	}
	
	@Override
	public AnalogInputReader getAnalogInput(int index) {
		if(angInputs[index] == null)
			angInputs[index] = new MockAnalogInput();
		return angInputs[index];
	}
	
	public RelayOutput getRelay(int index) {
		if(relays[index] == null)
			relays[index] = new MockRelay(index);
		return relays[index];
	}

	@Override
	public PositionReader getPositionSensor() {
		if (positionSensor == null)
			positionSensor = new MockPositionSensor();
		return positionSensor;
	}

	@Override
	public Clock getClock() {
		// TODO Replace this with a controlled clock
		return SystemClock.instance;
	}

	@Override
	public boolean hasNewDriverStationData() {
		boolean result = m_hasDriverStationUpdate;
		m_hasDriverStationUpdate = false;
		return result;
	}

	public void setHasNewDriverStationData() {
		m_hasDriverStationUpdate = true;
	}

	@Override
	public double getBatteryVoltage() {
		return m_batteryVoltage;
	}

	public void setBatteryVoltage(double voltage) {
		m_batteryVoltage = voltage;
	}
}
