package com.team766.robot.procedures;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.framework.LaunchedContext;
import com.team766.robot.Robot;
import com.team766.library.RateLimiter;
import com.team766.library.ValueProvider;
import com.team766.hal.RobotProvider;
import com.team766.odometry.Point;
import com.team766.odometry.PointDir;
import com.team766.hal.PositionReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.team766.config.ConfigFileReader;
import com.team766.logging.Category;
import com.team766.logging.Severity;
import com.team766.controllers.PIDController;
import edu.wpi.first.wpilibj.Filesystem;
import org.json.*;

public class FollowPoints extends Procedure {

	//Steps combine possible data types into one object for flexibility and ease-of-use purposes
	public static class Step {

		public PointDir wayPoint;
		public boolean criticalPoint;
		public Procedure procedure;
		public boolean stopRobot;
		// ...

		public Step(PointDir wayPoint, boolean criticalPoint, Procedure procedure, boolean stopRobot) {
			this.wayPoint = wayPoint;
			this.procedure = procedure;
		}
	}

	private List<Step> steps = new ArrayList<Step>();

	private static PointDir currentPos = new PointDir(0.0, 0.0, 0.0);
	private static PointDir lastPos = new PointDir(0.0, 0.0, 0.0);

	private PointDir[] pointList;
	private Procedure[] proceduresAtPoints;
	private boolean[] criticalPointList;
	private boolean[] stopRobotList;

	private int targetNum = 0;
	private RateLimiter followLimiter = new RateLimiter(0.05);

	//Radius defines the radius of the circle around the robot
	private static double radius = ConfigFileReader.getInstance().getDouble("trajectory.radius").get();
	private static double speed = ConfigFileReader.getInstance().getDouble("trajectory.speed").get();
	private static PointDir driveSettings = new PointDir(0, 0, 0);

	/*public FollowPoints() {
		parsePointList();
		proceduresAtPoints = new Procedure[pointList.length];
		for (int i = 0; i < proceduresAtPoints.length; i++) {
			proceduresAtPoints[i] = new DoNothing();
		}
		loggerCategory = Category.AUTONOMOUS;
	}*/

	public FollowPoints(String file) throws IOException {
		String str;
		Path path = Filesystem.getDeployDirectory().toPath().resolve(file);
		try {
			str = Files.readString(path);
		} catch (IOException e) {
			e.printStackTrace();
			log(Severity.ERROR, "Could not load " + file);
			return;
		}
		
		JSONArray points = new JSONObject(str).getJSONArray("points");
		for (int i = 0; i < points.length(); i++) {
			addStep(new PointDir(points.getJSONObject(0).getJSONArray("coordinates").getDouble(0), points.getJSONObject(0).getJSONArray("coordinates").getDouble(1), points.getJSONObject(0).getJSONArray("coordinates").getDouble(2)), points.getJSONObject(0).getBoolean("critical"), null, false);
		}
	}

	//Creates a new Step object from its constituents
	private void addStep(PointDir wayPoint, boolean criticalPoint, Procedure procedure, boolean stopRobot) {
		steps.add(new Step(wayPoint, criticalPoint, procedure, stopRobot));
	}

	//Default FollowPoints Constructor, Steps must be added here
	public FollowPoints() {
		addStep(new PointDir(0,0, 0), false, new DoNothing(), false);
		addStep(new PointDir(4,0, 90), true, null /* don't execute procedure */, false);
		addStep(new PointDir(0,0, 0), false, new DoNothing(), false);
		addWaypoints();
	}

	//When using steps, this sets up the arrays to be used by the FollowPoints method
	private void addWaypoints() {
		pointList = new PointDir[steps.size()];
		proceduresAtPoints = new Procedure[steps.size()];
		stopRobotList = new boolean[pointList.length];
		criticalPointList = new boolean[pointList.length];
		for (int i = 0; i < steps.size(); i++) {
			if (steps.get(i).wayPoint == null) continue;
			else {
				pointList[i] = steps.get(i).wayPoint;
			}
			if (steps.get(i).procedure != null) {
				proceduresAtPoints[i] = steps.get(i).procedure;
				// run procedure
				// TODO: make sure we're handling contexts correctly
			} else {
				proceduresAtPoints[i] = new DoNothing();
			}
			criticalPointList[i] = steps.get(i).criticalPoint;
			log(Boolean.toString(steps.get(i).criticalPoint));

			stopRobotList[i] = steps.get(i).stopRobot;
		}
	}

	//Takes an array of procedures and uses points from the config file
	public FollowPoints(Procedure[] procedureList) {
		parsePointList();
		proceduresAtPoints = procedureList;
		criticalPointList = new boolean[pointList.length];
		stopRobotList = new boolean[pointList.length];
		loggerCategory = Category.AUTONOMOUS;
	}

	//Takes an array of points
	public FollowPoints(PointDir[] points) {
		pointList = points;
		proceduresAtPoints = new Procedure[pointList.length];
		for (int i = 0; i < proceduresAtPoints.length; i++) {
			proceduresAtPoints[i] = new DoNothing();
		}
		criticalPointList = new boolean[pointList.length];
		stopRobotList = new boolean[pointList.length];
		loggerCategory = Category.AUTONOMOUS;
	}

	//Takes an array of points and an array of procedures to do at each point
	public FollowPoints(PointDir[] points, Procedure[] procedureList) {
		pointList = points;
		proceduresAtPoints = procedureList;
		criticalPointList = new boolean[pointList.length];
		stopRobotList = new boolean[pointList.length];
		loggerCategory = Category.AUTONOMOUS;
	}

	//Takes pairs of points from pointDoubles (set in the config file) and converts them to Points, which are placed in pointList.
	private void parsePointList() {
		Double[] pointDoubles = ConfigFileReader.getInstance().getDoubles("trajectory.points").get();
		pointList = new PointDir[pointDoubles.length / 2];
		double pointX = 0;
		double pointY = 0;
		for (int i = 0; i < pointDoubles.length / 2 * 2; i++) {
			if (i % 2 == 0)
				pointX = pointDoubles[i];
			else {
				pointY = pointDoubles[i];
				pointList[i / 2] = new PointDir(pointX, pointY);
			}
		}
	}

	public void run(Context context) {
		speed = ConfigFileReader.getInstance().getDouble("trajectory.speed").get();
		context.takeOwnership(Robot.drive);
		context.takeOwnership(Robot.gyro);
		log("Starting FollowPoints");
		//This resetCurrentPosition() call makes FollowPoints() robot-oriented instead of field-oriented
		//If we need to make this method field-oriented, just remove this line
		Robot.drive.resetCurrentPosition();
		Robot.gyro.resetGyro();
		targetNum = 0;

		for (int i = 0; i < pointList.length; i++) {
			log(Boolean.toString(criticalPointList[i]));
		}
		if (pointList.length > 0) {
			Point targetPoint = new Point(0.0, 0.0);
			currentPos.set(Robot.drive.getCurrentPosition().getX(), Robot.drive.getCurrentPosition().getY(), Robot.drive.getCurrentPosition().getHeading());
			while (targetNum != pointList.length - 1 ||  !passedPoint(pointList[pointList.length - 1])) {
				if (followLimiter.next()) {
					lastPos = currentPos.clone();
					currentPos.set(Robot.drive.getCurrentPosition().getX(), Robot.drive.getCurrentPosition().getY(), Robot.drive.getCurrentPosition().getHeading());
					//If the next point is a critical point, the robot will wait until it has passed that point for it to move to the next point
					//Otherwise, it uses the checkIntersection() method to follow the circle
					if (criticalPointList[targetNum]? passedPoint(pointList[targetNum]) : checkIntersection(targetNum, currentPos, pointList, radius)) {
						if (proceduresAtPoints.length < targetNum) {
							if (stopRobotList[targetNum]) {
								context.waitFor(context.startAsync(proceduresAtPoints[targetNum])); 
							} else {
								context.startAsync(proceduresAtPoints[targetNum]);
							}
						}
						targetNum++;
						log("Going to Next Point!");
					}
					targetPoint = selectTargetPoint(targetNum, currentPos, pointList, radius);
					//double diff = currentPos.getAngleDifference(targetPoint);
					//Robot.drive.setDrivePower(straightVelocity + Math.signum(diff) * Math.min(Math.abs(diff) * theBrettConstant, 1 - straightVelocity), straightVelocity - Math.signum(diff) * Math.min(Math.abs(diff) * theBrettConstant, 1 - straightVelocity));
					
					Robot.drive.setGyro(Robot.gyro.getGyroYaw());
					driveSettings.set(currentPos.scaleVector(targetPoint, speed), rotationSpeed(Robot.gyro.getGyroYaw(), pointList[targetNum].getHeading()));
					Robot.drive.swerveDrive(driveSettings);
					log("Current Position: " + currentPos.toString());
					log("Target Point: " + targetPoint.toString());
					log("Unit Vector: " + new PointDir(currentPos.scaleVector(targetPoint, speed), rotationSpeed(Robot.gyro.getGyroYaw(), pointList[targetNum].getHeading())).toString());

					context.yield();
				} else {
					updateRotation();
				}
			}
			Robot.drive.drive2D(0, 0);
			log("Finished method!");
		} else {
			log("No points!");
		}
	}

	public void updateRotation() {
		Robot.drive.setGyro(Robot.gyro.getGyroYaw());
		driveSettings.setHeading(rotationSpeed(Robot.gyro.getGyroYaw(), pointList[targetNum].getHeading()));
		Robot.drive.swerveDrive(driveSettings);
	}

	//Returns whether the circle around the robot intersects the line connecting the two next points.
	public static boolean checkIntersection(int targetNum, PointDir currentPos, Point[] pointList, double radius) {
		double a;
		double b;
		double c;
		double slope;
		if (targetNum < pointList.length - 1) {
			slope = pointList[targetNum].slope(pointList[targetNum + 1]);
			a = slope * slope + 1;
			b = -2 * currentPos.getX() - 2 * slope * slope * pointList[targetNum].getX() + 2 * slope * pointList[targetNum].getY() - 2 * currentPos.getY() * slope;
			c = currentPos.getX() * currentPos.getX() + slope * slope * pointList[targetNum].getX() * pointList[targetNum].getX() - 2 * slope * pointList[targetNum].getX() * pointList[targetNum].getY() + pointList[targetNum].getY() * pointList[targetNum].getY() + 2 * currentPos.getY() * slope * pointList[targetNum].getX() - 2 * currentPos.getY() * pointList[targetNum].getY() + currentPos.getY() * currentPos.getY() - radius * radius;
			if (b * b - 4 * a * c > 0) {
				return true;                                        
			}
		}
		return false;
	}

	//If the circle around the robot intersects the line connecting the previous and next points, returns whichever intersection point is closest to the next point. Otherwise, returns the next point.
	public static Point selectTargetPoint(int targetNum, PointDir currentPos, Point[] pointList, double radius) {
		double a;
		double b;
		double c;
		double slope;
		double potentialX1;
		double potentialX2;
		double potentialY1;
		double potentialY2;
		Point potentialPoint1 = new Point(0.0, 0.0);
		Point potentialPoint2 = new Point(0.0, 0.0);
		if (targetNum == 0) {
			return pointList[0];
		} else {
			slope = pointList[targetNum - 1].slope(pointList[targetNum]);
			a = slope * slope + 1;
			b = -2 * currentPos.getX() - 2 * slope * slope * pointList[targetNum - 1].getX() + 2 * slope * pointList[targetNum - 1].getY() - 2 * currentPos.getY() * slope;
			c = currentPos.getX() * currentPos.getX() + slope * slope * pointList[targetNum - 1].getX() * pointList[targetNum - 1].getX() - 2 * slope * pointList[targetNum - 1].getX() * pointList[targetNum - 1].getY() + pointList[targetNum - 1].getY() * pointList[targetNum - 1].getY() + 2 * currentPos.getY() * slope * pointList[targetNum - 1].getX() - 2 * currentPos.getY() * pointList[targetNum - 1].getY() + currentPos.getY() * currentPos.getY() - radius * radius;
			if (b * b - 4 * a * c < 0) {
				return pointList[targetNum];
			} else {
				potentialX1 = (-1 * b + Math.sqrt(b * b - 4 * a * c))/ (2 * a);
				potentialX2 = (-1 * b - Math.sqrt(b * b - 4 * a * c))/ (2 * a);
				potentialY1 = slope * (potentialX1 - pointList[targetNum - 1].getX()) + pointList[targetNum - 1].getY();
				potentialY2 = slope * (potentialX2 - pointList[targetNum - 1].getX()) + pointList[targetNum - 1].getY();
				potentialPoint1.set(potentialX1, potentialY1);
				potentialPoint2.set(potentialX2, potentialY2);
				if (potentialPoint1.distance(pointList[targetNum]) <= potentialPoint2.distance(pointList[targetNum])) {
					return potentialPoint1;
				} else {
					return potentialPoint2;
				}
			}
		}
	}

	//Returns if the robot has passed a certain point
	public boolean passedPoint(Point P) {
		log(currentPos + " " + P + " " + currentPos.distance(P) + " " + ((currentPos.distance(P) > lastPos.distance(P) && currentPos.distance(P) <= 0.2) ? " true" : " false"));
		return (currentPos.distance(P) > lastPos.distance(P) && currentPos.distance(P) <= 0.2);
	}

	//Returns a value between -1 and 1 corresponding to how much the robot should turn to reach the target point
	public double rotationSpeed(double currentRot, double targetRot) {
		double maxSpeed = 0.2;
		double angleDistanceForMaxSpeed = 90;
		currentRot = mod(currentRot, 360);
		targetRot = mod(targetRot, 360);
		if (Math.abs(targetRot - currentRot) > Math.abs(targetRot + 360 - currentRot)) {
			targetRot += 360;
		}
		if (Math.abs(targetRot - currentRot) > Math.abs(targetRot - 360 - currentRot)) {
			targetRot -= 360;
		}
		if (Math.abs(targetRot - currentRot) <= angleDistanceForMaxSpeed) {
			return -(currentRot - targetRot) / angleDistanceForMaxSpeed * maxSpeed;
		}
		return maxSpeed * -Math.signum(currentRot - targetRot);
	}

	public static double mod(double d1, double d2) {
		return d1 % d2 + (d1 < 0 ? d2 : 0);
	}
}