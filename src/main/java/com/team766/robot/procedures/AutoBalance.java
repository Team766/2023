package com.team766.robot.procedures;

import com.team766.framework.Procedure;
import com.team766.odometry.PointDir;
import java.util.ArrayList;
import java.util.List;
import com.team766.framework.Context;
import com.team766.robot.Robot;
import edu.wpi.first.wpilibj.DriverStation.Alliance;


public class AutoBalance extends Procedure {

	//private PointDir target;
	private List<PointDir> points;
	private boolean setMid;
	

	
	public void run(Context context) {
		context.takeOwnership(Robot.drive);
		// TODO: REMEMBER TO ADD var alliance = DriverStation.getAlliance();
		var alliance = Alliance.Blue;
		ChargeStationPathFinder finder = new ChargeStationPathFinder(alliance, setMid);
		PointDir[] points = finder.calculatePoints(Robot.drive.getCurrentPosition());

		context.startAsync(new FollowPoints(points));
	}

	public AutoBalance(boolean setMid) {
		this.setMid = setMid;
		points = new ArrayList<PointDir>();
	}

}
