package com.team766.frc2018;

import com.team766.framework.AutonomousCommand;
import com.team766.frc2018.commands.ExampleDriveSequence;
import com.team766.frc2018.commands.ExampleSequence;

public enum AutonomousModes {
	@AutonomousCommand(commandClass=ExampleDriveSequence.class)
	Autonomous1,
	@AutonomousCommand(commandClass=ExampleSequence.class)
	Autonomous2;
}
