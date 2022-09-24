package com.team766.hal.simulator;

import com.team766.hal.ControlInputReader;
import com.team766.hal.BasicSpeedController;
import com.team766.hal.LocalSpeedController;
import com.team766.simulator.ProgramInterface;

public class SimSpeedController extends LocalSpeedController {

	public SimSpeedController(int address) {
		this(ProgramInterface.canSpeedControllerChannels[address]);
	}

	SimSpeedController(ProgramInterface.CANSpeedControllerCommunication channel) {
		super(new BasicSpeedController() {
			@Override
			public double get() {
				return channel.command.output;
			}

			@Override
			public void set(double power) {
				channel.command.output = power;
				channel.command.controlMode =
					ProgramInterface.CANSpeedControllerCommand.ControlMode.PercentOutput;
			}

			@Override
			public void restoreFactoryDefault() {}
		}, new ControlInputReader() {
			@Override
			public double getPosition() {
				return channel.status.sensorPosition;
			}

			@Override
			public double getRate() {
				return channel.status.sensorVelocity;
			}
		});
	}
}
