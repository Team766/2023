package com.team766.frc2020;

import com.team766.framework.AutonomousCommand;
import com.team766.frc2020.commands.ExampleDriveSequence;

public enum AutonomousModes {
	@AutonomousCommand(commandClass=ExampleDriveSequence.class) ExampleDriveSequence,
}
