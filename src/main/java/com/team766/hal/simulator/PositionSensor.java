package com.team766.hal.simulator;

import com.team766.hal.PositionReader;
import com.team766.simulator.ProgramInterface;

public class PositionSensor implements PositionReader {

	@Override
	public double getX() {
		return ProgramInterface.robotPosition.x;
	}

	@Override
	public double getY() {
		return ProgramInterface.robotPosition.y;
	}

	@Override
	public double getHeading() {
		return ProgramInterface.robotPosition.heading;
	}

}