package com.team766.hal.wpilib;

import com.team766.EntryPoint;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Scheduler;
import com.team766.hal.MyRobot;
import com.team766.hal.RobotProvider;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;

public class RobotMain extends TimedRobot {
	private MyRobot robot;

	public static void main(String... args) {
		RobotBase.startRobot(RobotMain::new);
	}

	public RobotMain() {
		super(0.005);
	}

	@Override
	public void robotInit() {
		try {
			ConfigFileReader.instance = new ConfigFileReader("/home/lvuser/robotConfig.txt");
			RobotProvider.instance = new WPIRobotProvider();
			robot = EntryPoint.createRobot();
			
			robot.robotInit();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void disabledInit() {
		try{
			robot.disabledInit();
		}catch (Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	@Override
	public void autonomousInit() {
		try{
			robot.autonomousInit();
		}catch (Exception e){
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void teleopInit() {
		try{
			robot.teleopInit();
		}catch (Exception e){
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void disabledPeriodic() {
		try{
			robot.disabledPeriodic();
			Scheduler.getInstance().run();
		}catch (Exception e){
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void autonomousPeriodic() {
		try{
			robot.autonomousPeriodic();
			Scheduler.getInstance().run();
		}catch (Exception e){
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void teleopPeriodic() {
		try{
			robot.teleopPeriodic();
			Scheduler.getInstance().run();
		}catch (Exception e){
			e.printStackTrace();
			throw e;
		}
	}
}
