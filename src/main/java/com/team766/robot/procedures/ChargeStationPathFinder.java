package com.team766.robot.procedures;

import java.util.ArrayList;
import java.util.List;
import com.team766.framework.Context;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.odometry.PointDir;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class ChargeStationPathFinder {

	private static final Logger logger = Logger.get(Category.PROCEDURES);

	public static final double BLUE_BALANCE_TARGET_X = 3.9417625;
	public static final double BLUE_BALANCE_LEFT_EDGE = 2.974975;
	public static final double BLUE_BALANCE_RIGHT_EDGE = 4.90855;

	public static final double RED_BALANCE_TARGET_X = 12.5999875;
	public static final double RED_BALANCE_LEFT_EDGE = 11.6332;
	public static final double RED_BALANCE_RIGHT_EDGE = 13.566775;

	public static final double X_ALIGNMENT_THRESHOLD = 0.5;
	public static final double Y_ALIGNMENT_THRESHOLD = 0.5;

	public static final double BLUE_LEFT_PT = BLUE_BALANCE_LEFT_EDGE - X_ALIGNMENT_THRESHOLD;
	public static final double BLUE_RIGHT_PT = BLUE_BALANCE_RIGHT_EDGE + X_ALIGNMENT_THRESHOLD;
	public static final double RED_LEFT_PT = RED_BALANCE_LEFT_EDGE - X_ALIGNMENT_THRESHOLD;
	public static final double RED_RIGHT_PT = RED_BALANCE_RIGHT_EDGE + X_ALIGNMENT_THRESHOLD;

	public static final double CHARGE_TOP_EDGE = 3.96875;
	public static final double CHARGE_BOTTOM_EDGE = 1.4986;
	public static final double MIDDLE = 2.733675;

	private final Alliance alliance;
	private final boolean setMid;
	
	public ChargeStationPathFinder(Alliance alliance, boolean setMid) {
		this.alliance = alliance;
		this.setMid = setMid;
	}

	public ChargeStationPathFinder(Alliance alliance) {
		this(alliance, true);
	}

	public PointDir[] calculatePoints(PointDir curPos) {
		double curX = curPos.getX();
		double curY = curPos.getY();
		List<PointDir> points = new ArrayList<PointDir>();
		switch (alliance) {
			case Red:
				align(points, curX, curY, BLUE_BALANCE_TARGET_X, BLUE_BALANCE_LEFT_EDGE, BLUE_BALANCE_LEFT_EDGE);

				break;
				
			case Blue:
				align(points, curX, curY, RED_BALANCE_TARGET_X, RED_BALANCE_LEFT_EDGE, RED_BALANCE_LEFT_EDGE);

				break;

			case Invalid: // drop down
			default: 
				logger.logRaw(null, "Invalid Alliance");
		}

		return points.toArray(new PointDir[points.size()]);	
	}

		private void addPoints(List<PointDir> points, double curX, double curY, double target, double left, double right, double height) {
			if (curX > target) {
				if (curX < right)
					points.add(new PointDir(right, curY));	

				points.add(new PointDir(right, height));
			} else {
				if (curX > left)
					points.add(new PointDir(left, curY));

				points.add(new PointDir(left, height));
			}

			points.add(new PointDir(RED_BALANCE_TARGET_X, MIDDLE));
		}


		private void align(List<PointDir> points, double curX, double curY, double target, double left, double right) {
			if (setMid) {
				addPoints(points, curX, curY, target, left, right, MIDDLE);

			} else if (curY < CHARGE_TOP_EDGE - Y_ALIGNMENT_THRESHOLD && curY > CHARGE_BOTTOM_EDGE + Y_ALIGNMENT_THRESHOLD) {
				addPoints(points, curX, curY, target, left, right, curY);

			} else {
				logger.logRaw(null, "Robot not aligned with charging station");
			}
		}

	
}