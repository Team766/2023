package com.team766.robot;

import com.team766.framework.AutonomousMode;
import com.team766.robot.procedures.*;

public class AutonomousModes {
	public static final AutonomousMode[] AUTONOMOUS_MODES = new AutonomousMode[] {
		// Add autonomous modes here like this:
		//    new AutonomousMode("NameOfAutonomousMode", () -> new MyAutonomousProcedure()),
		//
		// If your autonomous procedure has constructor arguments, you can
		// define one or more different autonomous modes with it like this:
		//    new AutonomousMode("DriveFast", () -> new DriveStraight(1.0)),
		//    new AutonomousMode("DriveSlow", () -> new DriveStraight(0.4)),

		new AutonomousMode("DriveSquare", () -> new DriveSquare()),
		new AutonomousMode("TurnRight", () -> new TurnRight()),
		new AutonomousMode("DriveStraight", () -> new DriveStraight()),
		new AutonomousMode("DoNothing", () -> new DoNothing()),
	};
}