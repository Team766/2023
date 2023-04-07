package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;
import com.team766.robot.constants.FollowPointsInputConstants;
import java.util.function.BooleanSupplier;
import edu.wpi.first.wpilibj.DriverStation;
import com.team766.odometry.*;

public class OPECHelperFollowPoints extends Procedure {

	private static final double DIST = 4;

	public void run(Context context) {
		context.takeOwnership(Robot.drive);
		context.takeOwnership(Robot.intake);
		Robot.gyro.resetGyro180();
		Robot.drive.setGyro(Robot.gyro.getGyroYaw());
		new ReverseIntake().run(context);
		context.releaseOwnership(Robot.drive);
		switch (DriverStation.getAlliance()) {
			case Blue:
				new FollowPoints(new PointDir[]{new PointDir(4, 0, 180)}).run(context);
				break;
			case Red:
				new FollowPoints(new PointDir[]{new PointDir(-4, 0, 180)}).run(context);
				break;
			case Invalid: //drop down
			default: 
				log("invalid alliance");
				return;
		}
	}
}
