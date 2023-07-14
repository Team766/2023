package com.team766.controllers;

import com.team766.config.ConfigFileReader;
import com.team766.hal.RobotProvider;
import com.team766.library.SetValueProvider;
import com.team766.library.SettableValueProvider;
import com.team766.library.ValueProvider;
import com.team766.logging.Category; //Todo: ?
import com.team766.logging.Logger;
import com.team766.logging.Severity; // Todo: ?


import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;
import java.io.IOError;
import javax.swing.text.DefaultStyledDocument.ElementSpec;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.library.RateLimiter;


public class CSMMCPID{
	// The attributes of the class include references to the motor controller, SparkMax controller, PID controller, and absolute encoder
	private MotorController mc1;
	private CANSparkMax csm1;
	private SparkMaxPIDController pid1;
	private SparkMaxAbsoluteEncoder abs1;
	//PID Related Variables (should be private but whatever)
	private static double dz1 = 0; 
	private static double maxpos1 = 0;
	private static double minpos1 = 0;
	private static double maxvel1 = 0;
	private static double maxaccel1 = 0;
	private static double maxspeed1 = 0;
	private static double minspeed1 = 0;
	private static double currentPos = 0;
	private static double combo;

	//antigrav variable
	public static double antiGravK;
	//enum for which state the PID is in
	private enum PIDSTATE{
		PID,
		OFF,
		ANTIGRAV
	}
	//the state of the PID2
	private PIDSTATE theState = PIDSTATE.OFF;
	
	//constructor for the class with no absolute encoder
	public CSMMCPID(String configName) throws Exception{
			//loggerCategory = Category.MECHANISMS;

			try{
				mc1 = RobotProvider.instance.getMotor(configName);
				csm1 = (CANSparkMax)mc1;
				pid1 = csm1.getPIDController();
			}catch (IllegalArgumentException Ill){
				throw new Exception("Error instantiating the PID controller: " + Ill);
			}
			
		
	}
	//constructor for the class with an absolute encoder
	public CSMMCPID(String configName, double absEncoderOffset) throws Exception{
			//loggerCategory = Category.MECHANISMS;

			try{
				mc1 = RobotProvider.instance.getMotor(configName);
				csm1 = (CANSparkMax)mc1;
				pid1 = csm1.getPIDController();
				abs1 = csm1.getAbsoluteEncoder(Type.kDutyCycle);
				abs1.setZeroOffset(absEncoderOffset);
				pid1.setFeedbackDevice(abs1);
			}catch (IllegalArgumentException Ill){
				throw new Exception("Error instantiating the CLE PID controller: " + Ill);
			}
			
			
	}
	//manually changing the state
	public void updateState(PIDSTATE state){
		theState = state;
	}
	//changing all PID values at once
	public void setPIDF(double p, double i, double d, double ff){
		pid1.setP(p);
		pid1.setI(i);
		pid1.setD(d);
		pid1.setFF(ff);
	}
	//changing the P value
	public void setP(double p){
		pid1.setP(p);
	}
	//changing the I value
	public void setI(double i){
		pid1.setI(i);
	}
	//changing the D value
	public void setD(double d){
		pid1.setD(d);
	}
	//changing the FF value
	public void setFf(double ff){
		pid1.setFF(ff);
	}
	
	//setting the antigravity constants2
	public void setAntigravConstant(double k){
		antiGravK = k;
	}

	private void antigrav(){
		mc1.set(antiGravK * Math.sin(mc1.getSensorPosition()));
	}

	//adding a built in closed loop error (not tested yet)
	public void setSmartMotionAllowedClosedLoopError(double error){
		pid1.setSmartMotionAllowedClosedLoopError(error, 0);
	}
	//changing the deadzone
	public void setDeadzone(double dz){
		dz1 = dz;
	}
	//changing the output range of the speed of the motors
	public void setOutputRange(double min, double max){
		maxspeed1 = max;
		minspeed1 = min;
		pid1.setOutputRange(min, max);
	}
	//changing the neutral mode of the motor (brake/coast)
	public void setMotorMode(NeutralMode mode){
		mc1.setNeutralMode(mode);
	}
	//setting the maximum and minimul locations that the motor can go to
	public void setMinMaxLocation(double min, double max){
		maxpos1 = max;
		minpos1 = min;
	}
	//setting the maximum velocity of the motor
	public void setMaxVel(double max){
		maxvel1 = max;
		pid1.setSmartMotionMaxVelocity(max, 0);
		pid1.setSmartMotionMinOutputVelocity(0, 0);
	}
	//setting the maximum acceleration of the motor
	public void setMaxAccel(double max){
		maxaccel1 = max;
		pid1.setSmartMotionMaxAccel(max, 0);
	}

	//go to a position using the thing that wasn't tested yet and almost broke the robot...
	public void setCLEPosition(double position){
		if(position > maxpos1){
			position = maxpos1;
		} else if(position < minpos1){
			position = minpos1;
		}

		pid1.setReference(position, ControlType.kSmartMotion);
	}
	//1st step to go to a position using the normal PID, setting what you want the position to be
	public void setPosition(double position){
		if(position > maxpos1){
			position = maxpos1;
		} else if(position < minpos1){
			position = minpos1;
		}

		currentPos = position;
	}

	public void STOP(){
		//Failsafe
		currentPos = mc1.getSensorPosition();
		theState = PIDSTATE.OFF;
	}

	//run loop that actually runs the PID using normal dz
	//You need to call this function repedatly in OI
	public void run(boolean enabled){
		if(enabled){
			switch(theState){
				case OFF:
					break;
				case ANTIGRAV:
					antigrav();
				case PID:
					if (mc1.getSensorPosition() <= (dz1 + mc1.getSensorPosition()) && mc1.getSensorPosition() >= (mc1.getSensorPosition() - dz1)){
						combo ++;
					} else {
						combo = 0;
						pid1.setReference(currentPos, ControlType.kSmartMotion); // todo: testing if this is allowed
					}
		
					if(combo >= 6){
						theState = PIDSTATE.ANTIGRAV;
					}
					break;
			}
		} else{
			//log("enabled is false");
		}
		
	}

	}

