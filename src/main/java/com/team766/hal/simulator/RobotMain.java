package com.team766.hal.simulator;

import java.io.IOException;

import com.team766.config.ConfigFileReader;
import com.team766.framework.Scheduler;
import com.team766.hal.GenericRobotMain;
import com.team766.hal.RobotProvider;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.simulator.Program;
import com.team766.simulator.ProgramInterface;
import com.team766.simulator.Simulator;

public class RobotMain {
	static enum Mode {
		MaroonSim,
		VrConnector,
	}

	private GenericRobotMain robot;
	private Runnable simulator;
	
	public RobotMain(Mode mode) {
		try {
			ConfigFileReader.instance = new ConfigFileReader("simConfig.txt");
			RobotProvider.instance = new SimulationRobotProvider();
			
			Scheduler.getInstance().reset();
			
			robot = new GenericRobotMain();
			
			robot.robotInit();
			
			ProgramInterface.program = new Program() {
				ProgramInterface.RobotMode prevRobotMode = null;
				
				@Override
				public void step() {
					switch (ProgramInterface.robotMode) {
						case DISABLED:
							if (prevRobotMode != ProgramInterface.RobotMode.DISABLED) {
								robot.disabledInit();
								prevRobotMode = ProgramInterface.RobotMode.DISABLED;
							}
							robot.disabledPeriodic();
							Scheduler.getInstance().run();
							break;
						case AUTON:
							if (prevRobotMode != ProgramInterface.RobotMode.AUTON) {
								robot.autonomousInit();
								prevRobotMode = ProgramInterface.RobotMode.AUTON;
							}
							robot.autonomousPeriodic();
							Scheduler.getInstance().run();
							break;
						case TELEOP:
							if (prevRobotMode != ProgramInterface.RobotMode.TELEOP) {
								robot.teleopInit();
								prevRobotMode = ProgramInterface.RobotMode.TELEOP;
							}
							robot.teleopPeriodic();
							Scheduler.getInstance().run();
							break;
					}
				}

				@Override
				public void reset() {
					robot.resetAutonomousMode("simulation reset");
				}
			};
		} catch (Exception exc) {
			exc.printStackTrace();
			LoggerExceptionUtils.logException(exc);
		}

		switch (mode) {
		case MaroonSim:
			simulator = new Simulator();
			break;
		case VrConnector:
			ProgramInterface.robotMode = ProgramInterface.RobotMode.DISABLED;
			try {
				simulator = new VrConnector();
			} catch (IOException ex) {
				throw new RuntimeException("Error initializing communication with 3d Simulator", ex);
			}
			break;
		}

	}
	
	public void run(){
		try {
			simulator.run();
		} catch (Exception exc) {
			exc.printStackTrace();
			LoggerExceptionUtils.logException(exc);
		}
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
		case "-vr_connector":
			mode = Mode.VrConnector;
			break;
		default:
			System.err.println("Needs -maroon_sim or -vr_connector");
			System.exit(1);
			return;
		}
		new RobotMain(mode).run();
	}
}
