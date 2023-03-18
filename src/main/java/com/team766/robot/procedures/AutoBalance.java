package com.team766.robot.procedures;

import com.team766.framework.Procedure;
import com.team766.odometry.PointDir;
import com.team766.framework.Context;
import com.team766.robot.Robot;
import edu.wpi.first.wpilibj.DriverStation.Alliance;


public class AutoBalance extends Procedure {

	private boolean setMid;
	private Alliance alliance;
	

	
	public void run(Context context) {
		context.takeOwnership(Robot.drive);
		ChargeStationPathFinder finder = new ChargeStationPathFinder(alliance, setMid);
		PointDir[] points = finder.calculatePoints(Robot.drive.getCurrentPosition());

		context.startAsync(new FollowPoints(points));
	}

	public AutoBalance(boolean setMid, Alliance alliance) {
		this.setMid = setMid;
		this.alliance = alliance;
	}

	public AutoBalance(Alliance alliance) {
		this(true, alliance);
	}

}
