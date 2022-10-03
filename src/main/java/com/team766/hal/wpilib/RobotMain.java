package com.team766.hal.wpilib;

import java.util.function.Supplier;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Scheduler;
import com.team766.hal.GenericRobotMain;
import com.team766.hal.RobotProvider;
import com.team766.logging.LoggerExceptionUtils;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;

public class RobotMain extends TimedRobot {
	private GenericRobotMain robot;

	public static void main(String... args) {
		Supplier<RobotMain> supplier = new Supplier<RobotMain>() {
			RobotMain instance;
			@Override
			public RobotMain get() {
				if (instance == null) {
					instance = new RobotMain();
				}
				return instance;
			}
		};
		try {
			RobotBase.startRobot(supplier);
		} catch (Throwable ex) {
			ex.printStackTrace();
			LoggerExceptionUtils.logException(ex);
		}
	}

	public RobotMain() {
		super(0.005);
	}

	@Override
	public void robotInit() {
		try {
			ConfigFileReader.instance = new ConfigFileReader("/home/lvuser/robotConfig.txt");
			RobotProvider.instance = new WPIRobotProvider();
			robot = new GenericRobotMain();
			
			robot.robotInit();
		} catch (Exception e) {
			e.printStackTrace();
			LoggerExceptionUtils.logException(e);
		}
	}

	@Override
	public void disabledInit() {
		try{
			robot.disabledInit();
		}catch (Exception e){
			e.printStackTrace();
			LoggerExceptionUtils.logException(e);
		}
	}
	
	@Override
	public void autonomousInit() {
		try{
			robot.autonomousInit();
		}catch (Exception e){
			e.printStackTrace();
			LoggerExceptionUtils.logException(e);
		}
	}

	@Override
	public void teleopInit() {
		try{
			robot.teleopInit();
		}catch (Exception e){
			e.printStackTrace();
			LoggerExceptionUtils.logException(e);
		}
	}

	@Override
	public void disabledPeriodic() {
		try{
			robot.disabledPeriodic();
			Scheduler.getInstance().run();
		}catch (Exception e){
			e.printStackTrace();
			LoggerExceptionUtils.logException(e);
		}
	}

	@Override
	public void autonomousPeriodic() {
		try{
			robot.autonomousPeriodic();
			Scheduler.getInstance().run();
		}catch (Exception e){
			e.printStackTrace();
			LoggerExceptionUtils.logException(e);
		}
	}

	@Override
	public void teleopPeriodic() {
		try{
			robot.teleopPeriodic();
			Scheduler.getInstance().run();
		}catch (Exception e){
			e.printStackTrace();
			LoggerExceptionUtils.logException(e);
		}
	}
}
