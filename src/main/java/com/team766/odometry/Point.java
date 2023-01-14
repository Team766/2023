package com.team766.odometry;

import com.team766.framework.LoggingBase;
import java.lang.Math;

public class Point extends LoggingBase {
	private double x;
	private double y;

	public String getName() {
		return "Points";
	}

	public Point(double x, double y){
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void set(Point P) {
		this.x = P.getX();
		this.y = P.getY();
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double distance(Point a) {
		return Math.sqrt(Math.pow(a.getX() - getX(), 2.0) + Math.pow(a.getY() - getY(), 2.0));
	}

	public double slope(Point a) {
		double s;
		final int MAX = 1000; 
		if (a.getX() == getX()) {
			//If the points are on top of each other, returns positive or negative MAX.
			if (a.getY() < getY()) {
				s = -MAX;
			} else {
				s = MAX;
			}
		} else {
			s = (getY() - a.getY()) / (getX() - a.getX());
		}
		if (Math.abs(s) > MAX) {
			s = Math.signum(s) * MAX;
		}
		return s;
	}

	//Gets a vector with length scale between the point and another point. Used for swerve drive method input.
	public Point scaleVector(Point inputPoint, double scale) {
		double d = distance(inputPoint);
		return new Point((inputPoint.getX() - getX()) * scale / d, (inputPoint.getY() - getY()) * scale / d);
	}

	public Point add(Point p) {
		return new Point(getX() + p.getX(), getY() + p.getY());
	}

	public String toString() {
		return "X: " + getX() + " Y: " + getY();
	}
}