package com.team766.robot.procedures;

import java.util.ArrayList;
import java.util.List;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.odometry.PointDir;
import com.team766.robot.constants.ChargeConstants;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

// Helper class for AlignCharger, see AlignCharger.md for more details
public class ChargeStationPathFinder {

	// Since this class does not extend Procedure, we need to instantiate logger
	private static final Logger logger = Logger.get(Category.PROCEDURES);
	private final Alliance alliance;
	
	/**
	 * Constructor which takes alliance
	 * @param alliance Alliance for choosing which charge station to align to
	 */
	public ChargeStationPathFinder(Alliance alliance) {
		this.alliance = alliance;
	}

	/**
	 * Calculates points that the robot needs to follow based on alliance and current position
	 * @param curPos the current robot position, represented by a pointDir
	 * @return PointDir[]: list of the calculated points that can be entered into FollowPoints
	 */
	public PointDir[] calculatePoints(PointDir curPos) {

		// Sets curX and curY variables based on curPos param
		double curX = curPos.getX();
		double curY = curPos.getY();
	
		// Creates ArrayList for points
		List<PointDir> points = new ArrayList<PointDir>();
		switch (alliance) {
			case Red:
				// Calls addPoints method for red alliance coordinates
				addPoints(points, curX, curY, ChargeConstants.RED_BALANCE_TARGET_X, ChargeConstants.RED_LEFT_PT, ChargeConstants.RED_RIGHT_PT, ChargeConstants.MIDDLE);
				break;
				
			case Blue:
				// Calls addPoints method for red alliance coordinates
				addPoints(points, curX, curY, ChargeConstants.BLUE_BALANCE_TARGET_X, ChargeConstants.BLUE_LEFT_PT, ChargeConstants.BLUE_RIGHT_PT, ChargeConstants.MIDDLE);
				break;

			case Invalid: // drop down
			default: 
				logger.logRaw(null, "Invalid Alliance");
		}

		// Converts pointDir arrayList to array and returns it
		return points.toArray(new PointDir[points.size()]);	
	}

	/**
	 * Adds a set of points to an arrayList based on passed charge station coordinates
	 * @param points ArrayList to add points to
	 * @param curX Current robot X value
	 * @param curY Current robot Y value
	 * @param target X value of center of target charge station
	 * @param left X value of left side of target charge station
	 * @param right X value of right side of target charge station
	 * @param height Y value of center of target charge station
	 */
	private void addPoints(List<PointDir> points, double curX, double curY, double target, double left, double right, double height) {
		// If on the right side of the target, check if robot is blocked by the right side of the charge station, and add points accordingly
		if (curX > target) { 
			if (curX < right)
				points.add(new PointDir(right, curY));	

			points.add(new PointDir(right, height));

		// Otherwise, it will be on the left side of the target, so check if robot is blocked by the left side of the charge station, and add points accordingly
		} else {
			if (curX > left)
				points.add(new PointDir(left, curY));

			points.add(new PointDir(left, height));
		}
	}
	
}