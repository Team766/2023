package com.team766.hal.simulator;

import com.team766.config.ConfigFileReader;
import com.team766.framework.Scheduler;
import com.team766.hal.MyRobot;
import com.team766.hal.RobotProvider;
import com.team766.simulator.ProgramInterface;
import com.team766.simulator.Simulator;

public class RobotMain {
	private MyRobot robot;
	private Simulator simulator;
	
	public RobotMain() {
		ConfigFileReader.instance = new ConfigFileReader(this.getClass().getClassLoader().getResource("simConfig.txt").getPath());
		RobotProvider.instance = new SimulationRobotProvider();
		
		Scheduler.getInstance().reset();
		
		robot = new com.team766.frc2018.Robot();
		
		robot.robotInit();
		robot.autonomousInit();
		
		ProgramInterface.programStep = Scheduler.getInstance();
		
		simulator = new Simulator();
	}
	
	public void run(){
		simulator.run();
	}
	
	public static void main(String[] args) {
		new RobotMain().run();
	}
}
