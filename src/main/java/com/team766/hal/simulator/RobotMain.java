package com.team766.hal.simulator;

import com.team766.EntryPoint;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Scheduler;
import com.team766.hal.MyRobot;
import com.team766.hal.RobotProvider;
import com.team766.simulator.Parameters;
import com.team766.simulator.ProgramInterface;
import com.team766.simulator.Simulator;

public class RobotMain {
	private MyRobot robot;
	private Simulator simulator;
	
	public RobotMain() {
		ConfigFileReader.instance = new ConfigFileReader("simConfig.txt");
		RobotProvider.instance = new SimulationRobotProvider();
		
		Scheduler.getInstance().reset();
		
		robot = EntryPoint.createRobot();
		
		robot.robotInit();
		
		if (Parameters.RUN_AUTONOMOUS_MODE) {
			robot.autonomousInit();
			
			ProgramInterface.programStep = () -> {
				robot.autonomousPeriodic();
				Scheduler.getInstance().run();
			};
		} else {
			robot.teleopInit();
			
			ProgramInterface.programStep = () -> {
				robot.teleopPeriodic();
				Scheduler.getInstance().run();
			};
		}
		
		simulator = new Simulator();
	}
	
	public void run(){
		simulator.run();
	}
	
	public static void main(String[] args) {
		new RobotMain().run();
	}
}
