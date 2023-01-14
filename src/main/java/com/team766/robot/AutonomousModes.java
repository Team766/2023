package com.team766.robot;

import com.team766.framework.AutonomousMode;
import com.team766.robot.procedures.*;
import com.team766.odometry.Point;
import com.team766.odometry.PointDir;

public class AutonomousModes {
	public static final AutonomousMode[] AUTONOMOUS_MODES = new AutonomousMode[] {
		// Add autonomous modes here like this:
		//    new AutonomousMode("NameOfAutonomousMode", () -> new MyAutonomousProcedure()),
		//
		// If your autonomous procedure has constructor arguments, you can
		// define one or more different autonomous modes with it like this:
		//    new AutonomousMode("DriveFast", () -> new DriveStraight(1.0)),
		//    new AutonomousMode("DriveSlow", () -> new DriveStraight(0.4)),
		new AutonomousMode("FollowPoints", () -> new FollowPoints()),
		new AutonomousMode("FollowPointsH", () -> new FollowPoints(new Point[]{new Point(0, 0), new Point(2, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(0, 1)})),
		new AutonomousMode("DoNothing", () -> new DoNothing()),
	};
}