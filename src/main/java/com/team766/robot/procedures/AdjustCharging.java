package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.odometry.PointDir;
import com.team766.robot.Robot;


public class AdjustCharging extends Procedure {

	private PointDir gyroValue = new PointDir(0, 0, 0);
	private double pitch = Robot.gyro.getGyroPitch();

	private final double LEVEL = 3.0;
	private final double TOP_TILT = 15.0;
	private final double RAMP_LEVEL = 34.0;
	private final double RAMP_TILT = 11;

	public void run (Context context) {
		context.takeOwnership(Robot.gyro);
	}

	private PointDir calculateGyroValue() { 
		//TODO: test whether gyro value is absolute, and if so, is it roll or pitch
		//assuming it is absolute and yaw
		PointDir gyroValue = new PointDir(0, 0, 0);
		if (pitch < LEVEL) {
			return gyroValue;
		} //else if ()
	}

	private int adjustSign(double angle) {
		return (angle > 0) ? (1) : (-1);
 	}
}
