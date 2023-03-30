package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;
import com.team766.robot.constants.FollowPointsInputConstants;
import java.util.function.BooleanSupplier;
import edu.wpi.first.wpilibj.DriverStation;
import com.team766.odometry.*;

public class OPECHelper extends Procedure {

	private static final double DIST = 4;

	public void run(Context context) {
		context.takeOwnership(Robot.drive);
		context.takeOwnership(Robot.intake);
		double startX = Robot.drive.getCurrentPosition().getX();
		Robot.gyro.resetGyro180();
		Robot.drive.setGyro(Robot.gyro.getGyroYaw());
		new ReverseIntake().run(context);
		Robot.drive.swerveDrive(0, FollowPointsInputConstants.SPEED, 0);
		context.waitFor(() -> Math.abs(Robot.drive.getCurrentPosition().getX() - startX) > DIST);
		Robot.drive.stopDriveMotors();
		Robot.drive.stopSteerMotors();
	}
}
