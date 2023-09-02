package com.team766.robot.mechanisms;

import com.team766.controllers.PIDController;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAlternateEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Kicker {
	
	private MotorController motor = RobotProvider.instance.getMotor("kickerMotor");
	private CANSparkMax m_motor;
	private static final SparkMaxAlternateEncoder.Type kAltEncType = SparkMaxAlternateEncoder.Type.kQuadrature;
	private static final int kCPR = 8192;
	private static final MotorType kMotorType = MotorType.kBrushless;

/*
   * An alternate encoder object is constructed using the GetAlternateEncoder() 
   * method on an existing CANSparkMax object. If using a REV Through Bore 
   * Encoder, the type should be set to quadrature and the counts per 
   * revolution set to 8192
*/
  	

	

	private RelativeEncoder alternateEncoder = m_motor.getAlternateEncoder(kAltEncType, kCPR);


	private PIDController pid = new PIDController(0.1, 0, 0, 0, -1.0,1.0,0.005); //TODO: CALIBRATE VALUES
	private final double firstPoint = 0.000; //TODO: CHANGE
	private final double secondPoint = 0.000; //TODO: CHANGE

	private double curSetpoint;

	public Kicker(){
		m_motor = new CANSparkMax(0, kMotorType);
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
		pid.calculate(alternateEncoder.getPosition());
		motor.set(pid.getOutput());
	}

}
