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
	static enum Mode {
		MaroonSim,
		VrConnector,
	}

	private MyRobot robot;
	private Runnable simulator;
	
	public RobotMain(Mode mode) {
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
		
		switch (mode) {
		case Mode.MaroonSim:
			simulator = new Simulator();
			break;
		case Mode.VrConnector:
			simulator = new VrConnector();
			break;
		}

	}
	
	public void run(){
		simulator.run();
	}
	
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Needs -maroon_sim or -vr_connector");
			System.exit(1);
		}
		Mode mode;
		switch (args[0]) {
		case "-maroon_sim":
			mode = Mode.MaroonSim;
			break;
		case "-ve_connector":
			mode = Mode.VrConnector;
			break;
		default:
			System.err.println("Needs -maroon_sim or -vr_connector");
			System.exit(1);
		}
		new RobotMain().run(mode);
	}
}
