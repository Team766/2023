package com.team766.hal.mock;

import com.team766.hal.PositionReader;

public class MockPositionSensor implements PositionReader {

	private double x = 0;
	private double y = 0;
	private double heading = 0;

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public double getHeading() {
		return heading;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setHeading(double heading) {
		this.heading = heading;
	}

}