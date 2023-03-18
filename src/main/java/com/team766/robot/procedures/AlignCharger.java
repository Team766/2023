package com.team766.robot.procedures;

import com.team766.framework.Procedure;
import com.team766.odometry.PointDir;
import com.team766.framework.Context;
import com.team766.robot.Robot;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

// Procedure to align robot to a position where it can easily balance on the charge station
// See AlignCharger.md for more details
public class AlignCharger extends Procedure {

	private Alliance alliance;
	

	
	public void run(Context context) {
		context.takeOwnership(Robot.drive); 
		// Uses the ChargeStationPathFinder helper class to find correct points
		ChargeStationPathFinder finder = new ChargeStationPathFinder(alliance);
		PointDir[] points = finder.calculatePoints(Robot.drive.getCurrentPosition());

		// Calls FollowPoints on the calculated points
		context.startAsync(new FollowPoints(points));
	}

	/**
	 * Constructor which takes alliance
	 * @param alliance Alliance for choosing which charge station to align to
	 */
	public AlignCharger(Alliance alliance) {
		this.alliance = alliance;
	}


}
