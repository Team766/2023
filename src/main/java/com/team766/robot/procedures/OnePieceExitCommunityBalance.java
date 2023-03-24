package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.odometry.PointDir;
import com.team766.robot.Robot;

import edu.wpi.first.wpilibj.DriverStation;

public class OnePieceExitCommunityBalance extends Procedure {
	public void run (Context context) {
		context.takeOwnership(Robot.drive);
		switch (DriverStation.getAlliance()) {
			case Blue:
				Robot.drive.setCurrentPosition(new PointDir(2, 2.7));
				break;
			case Red:
				Robot.drive.setCurrentPosition(new PointDir(14.5, 2.7));
				break;
			case Invalid: //drop down
			default: 
				log("invalid alliance");
				return;
		}
		new OnePieceExitCommunity().run(context);
		new GyroBalance(DriverStation.getAlliance()).run(context);
	}
	
}
