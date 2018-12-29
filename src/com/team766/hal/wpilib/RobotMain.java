package com.team766.hal.wpilib;

import com.team766.config.ConfigFileReader;
import com.team766.framework.Scheduler;
import com.team766.hal.MyRobot;
import com.team766.hal.RobotProvider;

import edu.wpi.first.wpilibj.IterativeRobot;

public class RobotMain extends IterativeRobot {
	private MyRobot robot;

	@Override
	public void robotInit() {
		try{
			ConfigFileReader.instance = new ConfigFileReader("/home/lvuser/robotConfig.txt");
			RobotProvider.instance = new WPIRobotProvider();
			
			robot = new com.team766.frc2018.Robot();
			
			robot.robotInit();
		}catch (Exception e){
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
			Scheduler.getInstance().run();
			robot.disabledPeriodic();
		}catch (Exception e){
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void autonomousPeriodic() {
		try{
			Scheduler.getInstance().run();
			robot.autonomousPeriodic();
		}catch (Exception e){
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void teleopPeriodic() {
		try{
			Scheduler.getInstance().run();
			robot.teleopPeriodic();
		}catch (Exception e){
			e.printStackTrace();
			throw e;
		}
	}
}