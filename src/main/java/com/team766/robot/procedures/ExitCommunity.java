package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;
import com.team766.robot.constants.FollowPointsInputConstants;

public class ExitCommunity extends Procedure {

	private static final double DIST = 4;

	public void run(Context context) {
		context.takeOwnership(Robot.drive);
		double startY = Robot.drive.getCurrentPosition().getY();
		Robot.drive.controlFieldOriented(Math.toRadians(Robot.gyro.getGyroYaw()), 0, -FollowPointsInputConstants.SPEED, 0);
		context.waitFor(() -> Math.abs(Robot.drive.getCurrentPosition().getY() - startY) > DIST);
		Robot.drive.stopDrive();
	}
}
