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


	public void kick(){
		curSetpoint = firstPoint;
	}

	public void reset(){
		curSetpoint = secondPoint;
	}

	public void run(){
		pid.setSetpoint(curSetpoint);
		//pid.calculate( ABSOLUTE ENCODER POSITION);
		motor.set(pid.getOutput());
	}

}
