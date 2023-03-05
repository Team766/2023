package com.team766.robot.procedures;

import java.util.ArrayList;
import java.util.List;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.odometry.PointDir;
import com.team766.robot.constants.ChargeConstants;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class ChargeStationPathFinder {

	private static final Logger logger = Logger.get(Category.PROCEDURES);

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
				align(points, curX, curY, ChargeConstants.BLUE_BALANCE_TARGET_X, ChargeConstants.BLUE_LEFT_PT, ChargeConstants.BLUE_RIGHT_PT);

				break;
				
			case Blue:
				align(points, curX, curY, ChargeConstants.RED_BALANCE_TARGET_X, ChargeConstants.RED_LEFT_PT, ChargeConstants.RED_RIGHT_PT);

				break;

			case Invalid: // drop down
			default: 
				logger.logRaw(null, "Invalid Alliance");
		}

		return points.toArray(new PointDir[points.size()]);	
	}

		private void addPoints(List<PointDir> points, double curX, double curY, double target, double left, double right, double height, boolean isMid) {
			if (isMid) {
				if (curX > target) { // TODO: account for if robot is already on the charge station
					if (curX < right)
						points.add(new PointDir(right, curY));	

					points.add(new PointDir(right, height));
				} else {
					if (curX > left)
						points.add(new PointDir(left, curY));

					points.add(new PointDir(left, height));
				}
			}

			points.add(new PointDir(ChargeConstants.RED_BALANCE_TARGET_X, ChargeConstants.MIDDLE));
		}


		private void align(List<PointDir> points, double curX, double curY, double target, double left, double right) {
			if (setMid) {
				addPoints(points, curX, curY, target, left, right, ChargeConstants.MIDDLE, true);

			} else if (curY < ChargeConstants.CHARGE_TOP_EDGE - ChargeConstants.Y_ALIGNMENT_THRESHOLD && curY > ChargeConstants.CHARGE_BOTTOM_EDGE + ChargeConstants.Y_ALIGNMENT_THRESHOLD) {
				addPoints(points, curX, curY, target, left, right, curY, true);

			} else {
				logger.logRaw(null, "Robot not aligned with charging station");
			}
		}

	
}