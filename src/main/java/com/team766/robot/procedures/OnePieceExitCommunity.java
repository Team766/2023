package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.odometry.PointDir;
import com.team766.robot.Robot;

import edu.wpi.first.wpilibj.DriverStation;

public class OnePieceExitCommunity extends Procedure {
	public void run (Context context) {
		context.takeOwnership(Robot.drive);
		context.takeOwnership(Robot.intake);
		context.takeOwnership(Robot.gyro);
		Robot.gyro.setGyro(90);
		switch (DriverStation.getAlliance()) {
			case Blue:
				Robot.drive.setCurrentPosition(new PointDir(2, 0.75));
				break;
			case Red:
				Robot.drive.setCurrentPosition(new PointDir(14.5, 0.75));
				break;
			case Invalid: //drop down
			default: 
				log("invalid alliance");
				return;
		}
		log("exiting");
		new OPECHelper().run(context);
	}
}