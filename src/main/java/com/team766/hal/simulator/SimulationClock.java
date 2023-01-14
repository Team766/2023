package com.team766.hal.simulator;

import com.team766.hal.Clock;
import com.team766.simulator.ProgramInterface;

public class SimulationClock implements Clock {
	
	public static final SimulationClock instance = new SimulationClock();
	
	@Override
	public double getTime() {
		return ProgramInterface.simulationTime;
	}

}
