package com.team766.hal.simulator;

import com.team766.hal.ControlInputReader;
import com.team766.hal.BasicMotorController;
import com.team766.hal.LocalMotorController;
import com.team766.simulator.ProgramInterface;

public class SimMotorController extends LocalMotorController {

	public SimMotorController(int address) {
		this(ProgramInterface.canMotorControllerChannels[address]);
	}

	SimMotorController(ProgramInterface.CANMotorControllerCommunication channel) {
		super(new BasicMotorController() {
			@Override
			public double get() {
				return channel.command.output;
			}

			@Override
			public void set(double power) {
				channel.command.output = power;
				channel.command.controlMode =
					ProgramInterface.CANMotorControllerCommand.ControlMode.PercentOutput;
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
