package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.odometry.PointDir;
import com.team766.robot.Robot;
import com.team766.robot.mechanisms.Intake.GamePieceType;

import edu.wpi.first.wpilibj.DriverStation;

public class OnePieceExitCommunity extends Procedure {
	private final GamePieceType type;

	public OnePieceExitCommunity(GamePieceType type) {
		this.type = type;
	}
	public void run (Context context) {
		context.takeOwnership(Robot.drive);
		//context.takeOwnership(Robot.intake);
		context.takeOwnership(Robot.gyro);
		Robot.gyro.resetGyro180();
		switch (DriverStation.getAlliance()) {
			case Blue:
				Robot.drive.setCurrentPosition(new PointDir(0.75, 2));
				break;
			case Red:
				Robot.drive.setCurrentPosition(new PointDir(0.75, 14.5));
				break;
			case Invalid: //drop down
			default: 
				log("invalid alliance");
				return;
		}
		log("exiting");
		new ScoreHigh(type).run(context);
		new RetractWristvator().run(context);
		new ExitCommunity().run(context);
	}
}