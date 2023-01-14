package com.team766.odometry;

import com.team766.logging.Category;
import java.lang.Math;

public class PointDir extends Point {
	private double heading;

	public PointDir(double x, double y, double h){
		super(x, y);
		heading = h;
		loggerCategory = Category.DRIVE;
	}

	public PointDir(Point P, double h) {
		super(P.getX(), P.getY());
		heading = h;
		loggerCategory = Category.DRIVE;
	}

	public double getHeading() {
		return heading;
	}

	public void set(double x, double y, double h) {
		super.set(x, y);
		heading = h;
	}

	public void set(Point P, double h) {
		super.set(P);
		heading = h;
	}

	public void setHeading(double h) {
		heading = h;
	}

	public double getAngleDifference(Point a) {
		//Returns a number between -1 and 1 to represent the number of rotations between the two angles.
		PointDir unitVector;
		if (distance(a) == 0) {
			unitVector = new PointDir(1, 0, 0);
		} else {
			unitVector = new PointDir((a.getX() - getX()) / distance(a), (a.getY() - getY()) / distance(a), Math.toDegrees(Math.atan2((a.getY() - getY()) / distance(a), (a.getX() - getX()) / distance(a))));
		}
		double headingAngle = getHeading() % 360;
		if (headingAngle < 0) {
			headingAngle += 360;
		}
		if (unitVector.getHeading() < 0) {
			unitVector.setHeading(unitVector.getHeading() + 360);
		}
		double diff = headingAngle - unitVector.getHeading();
		if (diff < -180) {
			diff += 360;
		} else if (diff > 180) {
			diff -= 360;
		}
		return diff / 180;
	}

	public String toString() {
		return super.toString() + " Heading: " + getHeading();
	}

	public PointDir clone() {
		return new PointDir(getX(), getY(), getHeading());
	}
}
