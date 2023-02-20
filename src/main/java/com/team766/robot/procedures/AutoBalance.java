package com.team766.robot.procedures;

import com.team766.framework.Procedure;
import com.team766.odometry.PointDir;
import java.util.ArrayList;
import java.util.List;
import com.team766.framework.Context;
import com.team766.robot.Robot;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;


public class AutoBalance extends Procedure {

	public static final double BLUE_BALANCE_TARGET_X = 0.9667875;
	public static final double BLUE_BALANCE_LEFT_EDGE = 2.974975;
	public static final double BLUE_BALANCE_RIGHT_EDGE = 4.90855;

	public static final double RED_BALANCE_TARGET_X = 12.5999875;
	public static final double RED_BALANCE_LEFT_EDGE = 11.6332;
	public static final double RED_BALANCE_RIGHT_EDGE = 13.566775;

	public static final double X_ALIGNMENT_THRESHOLD = 0.5;

	public static final double BLUE_LEFT_PT = BLUE_BALANCE_LEFT_EDGE - X_ALIGNMENT_THRESHOLD;
	public static final double BLUE_RIGHT_PT = BLUE_BALANCE_RIGHT_EDGE + X_ALIGNMENT_THRESHOLD;
	public static final double RED_LEFT_PT = RED_BALANCE_LEFT_EDGE - X_ALIGNMENT_THRESHOLD;
	public static final double RED_RIGHT_PT = RED_BALANCE_RIGHT_EDGE + X_ALIGNMENT_THRESHOLD;


	public static final double MIDDLE = 1.235075;

	//private PointDir target;
	private List<PointDir> points;
	private boolean setMid;
	

	
	public void run (Context context) {
		context.takeOwnership(Robot.drive);
		switch (DriverStation.getAlliance()) {
			case Red:
				charge(setMid ? (new PointDir(RED_BALANCE_TARGET_X, MIDDLE)) : (new PointDir(RED_BALANCE_TARGET_X, Robot.drive.getCurrentPosition().getY())), context);
			case Blue: 
				charge(setMid ? (new PointDir(BLUE_BALANCE_TARGET_X, MIDDLE)) : (new PointDir(BLUE_BALANCE_TARGET_X, Robot.drive.getCurrentPosition().getY())), context);
			case Invalid: // drop down
			default:
				log("Invalid Alliance");
			
		}
	}


	public AutoBalance(boolean setMid) {
		this.setMid = setMid;
		points = new ArrayList<PointDir>();
	}

	private void alignCharging(PointDir target) {
		double curX = Robot.drive.getCurrentPosition().getX();
		double curY = Robot.drive.getCurrentPosition().getY();
		switch (DriverStation.getAlliance()) {
			case Red:
				if (curX > RED_BALANCE_TARGET_X) {
					points.add(new PointDir(RED_RIGHT_PT, curY));	
					points.add(new PointDir(RED_RIGHT_PT, target.getY()));
				} else {
					points.add(new PointDir(RED_LEFT_PT, curY));
					points.add(new PointDir(RED_LEFT_PT, target.getY()));
				}
				break;
				
			case Blue:
				if (curX > BLUE_BALANCE_TARGET_X) {
					points.add(new PointDir(BLUE_RIGHT_PT, curY));	
					points.add(new PointDir(BLUE_RIGHT_PT, target.getY()));
				} else {
					points.add(new PointDir(BLUE_LEFT_PT, curY));
					points.add(new PointDir(BLUE_LEFT_PT, target.getY()));
				}
				break;
			case Invalid: // drop down
			default:
				log("Invalid Alliance");
		}
	}

	private void charge(PointDir target, Context context) {
		alignCharging(target);
		points.add(target);
		context.startAsync(new FollowPoints((points.toArray(new PointDir[points.size()]))));
	}



}
