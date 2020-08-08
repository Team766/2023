package com.team766.hal.simulator;

import com.team766.config.ConfigFileReader;
import com.team766.framework.Scheduler;
import com.team766.hal.MyRobot;
import com.team766.hal.RobotProvider;
import com.team766.simulator.ProgramInterface;
import com.team766.simulator.Simulator;

public class RobotMain {
	// Run autonomous if true, else run teleop 
	private static final boolean RUN_AUTONOMOUS_MODE = true;
	
	private MyRobot robot;
	private Simulator simulator;
	
	public RobotMain() {
		ConfigFileReader.instance = new ConfigFileReader(this.getClass().getClassLoader().getResource("simConfig.txt").getPath());
		RobotProvider.instance = new SimulationRobotProvider();
		
		Scheduler.getInstance().reset();
		
		robot = new com.team766.frc2018.Robot();
		
		robot.robotInit();
		
		if (RUN_AUTONOMOUS_MODE) {
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
