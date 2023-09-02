package com.team766.robot.mechanisms;

import com.team766.controllers.PIDController;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class Kicker {
	
	private MotorController motor = RobotProvider.instance.getMotor("kickerMotor");
	private PIDController pid = new PIDController(0.1, 0, 0, 0, -1.0,1.0,0.005); //TODO: CALIBRATE VALUES
	private final double firstPoint = 0.000; //TODO: CHANGE
	private final double secondPoint = 0.000; //TODO: CHANGE

	private double curSetpoint;
	public Kicker(){

	}

	/*
	 * This is the method to use the kicker to kick
	 */
	public void kick(){
		curSetpoint = firstPoint;
	}

	/*
	 * This is the method to reset the kicker to its origonal position so it can be kicked again
	 */
	public void reset(){
		curSetpoint = secondPoint;
	}

	/*
	 * This is the run method to run the PIDs.
	 * It needs to be called in OI as frequently as possible.
	 */
	public void run(){
		pid.setSetpoint(curSetpoint);
		//pid.calculate( ABSOLUTE ENCODER POSITION);
		motor.set(pid.getOutput());
	}

}
