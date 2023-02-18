package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.SolenoidController;
import com.team766.hal.AnalogInputReader;

public class TestSolenoid extends Mechanism {
	private SolenoidController pusher;
	private AnalogInputReader analogReader;


	public TestSolenoid() {
		pusher = RobotProvider.instance.getSolenoid("launch");
		analogReader = RobotProvider.instance.getAnalogInput("analog");
	}


	public void setPusher(boolean extended) {
		checkContextOwnership();

		pusher.set(extended);
	}

	public double getAnalog() {
		return analogReader.getVoltage();
	}



}
