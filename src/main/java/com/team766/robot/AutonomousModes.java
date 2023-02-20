package com.team766.robot;

import com.team766.framework.AutonomousMode;
import com.team766.robot.procedures.*;
import com.team766.odometry.Point;
import com.team766.odometry.PointDir;
import com.team766.framework.Procedure;

public class AutonomousModes {

	public static final AutonomousMode[] AUTONOMOUS_MODES = new AutonomousMode[] {
		// Add autonomous modes here like this:
		//    new AutonomousMode("NameOfAutonomousMode", () -> new MyAutonomousProcedure()),
		//
		// If your autonomous procedure has constructor arguments, you can
		// define one or more different autonomous modes with it like this:
		//    new AutonomousMode("DriveFast", () -> new DriveStraight(1.0)),
		//    new AutonomousMode("DriveSlow", () -> new DriveStraight(0.4)),
		//new AutonomousMode("FollowPoints", () -> new FollowPoints()),
		new AutonomousMode("FollowPointsFile", () -> new FollowPoints("FollowPoints.json")),
		//new AutonomousMode("FollowPointsH", () -> new FollowPoints(new PointDir[]{new PointDir(0, 0), new PointDir(2, 0), new PointDir(1, 0), new PointDir(1, 1), new PointDir(2, 1), new PointDir(0, 1)})),
		new AutonomousMode("DoNothing", () -> new DoNothing()),
	};
}