package com.team766.hal.mock;

import com.team766.hal.MyRobot;

public class RobotTestCase extends TestCase{
	protected MyRobot robot;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		robot = new com.team766.frc2018.Robot();
		
		robot.robotInit();
	}
}
