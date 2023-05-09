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
import javax.swing.text.DefaultStyledDocument.ElementSpec;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.library.RateLimiter;


public class CSMMCPID {
	private MotorController mc1; 
    private CANSparkMax csm1;
    private SparkMaxPIDController pid1;
    private SparkMaxAbsoluteEncoder abs1;
	public static double dz1 = 0; 
	public static double maxpos1 = 0;
	public static double minpos1 = 0;
	public static double maxvel1 = 0;
	public static double maxaccel1 = 0;
	public static double maxspeed1 = 0;
	public static double minspeed1 = 0;
	public static double antiGrav1 = 0;
	public static double currentPos = 0;
	public static double combo = 0;
	private enum PIDSTATE{
		PID,
		OFF,
		ANTIGRAV
	}

	private PIDSTATE theState = PIDSTATE.OFF;


	public CSMMCPID(String configName){
			mc1 = RobotProvider.instance.getMotor(configName);
			csm1 = (CANSparkMax)mc1;
			pid1 = csm1.getPIDController();
		
	}

	public CSMMCPID(String configName, double absEncoderOffset){
			mc1 = RobotProvider.instance.getMotor(configName);
			csm1 = (CANSparkMax)mc1;
			pid1 = csm1.getPIDController();
			abs1 = csm1.getAbsoluteEncoder(Type.kDutyCycle);
			abs1.setZeroOffset(absEncoderOffset);
			pid1.setFeedbackDevice(abs1);
	}

	public void setPIDF(double p, double i, double d, double ff){
		pid1.setP(p);
		pid1.setI(i);
		pid1.setD(d);
		pid1.setFF(ff);
	}

	public void setP(double p){
		pid1.setP(p);
	}

	public void setI(double i){
		pid1.setI(i);
	}

	public void setD(double d){
		pid1.setD(d);
	}

	public void setFf(double ff){
		pid1.setFF(ff);
	}

	public void setSmartMotionAllowedClosedLoopError(double error){
		pid1.setSmartMotionAllowedClosedLoopError(error, 0);
	}

	public void setDeadzone(double dz){
		dz1 = dz;
	}

	public void setOutputRange(double min, double max){
		maxspeed1 = max;
		minspeed1 = min;
		pid1.setOutputRange(min, max);
	}

	public void setMotorMode(NeutralMode mode){
		mc1.setNeutralMode(mode);
	}

	public void setMaxMinLocation(double max, double min){
		maxpos1 = max;
		minpos1 = min;
	}

	public void setMaxVel(double max){
		maxvel1 = max;
		pid1.setSmartMotionMaxVelocity(max, 0);
		pid1.setSmartMotionMinOutputVelocity(0, 0);
	}

	public void setMaxAccel(double max){
		maxaccel1 = max;
		pid1.setSmartMotionMaxAccel(max, 0);
	}


	public void magicallyGoToPositionUsingCLE(double position){
		if(position > maxpos1){
			position = maxpos1;
		} else if(position < minpos1){
			position = minpos1;
		}

		pid1.setReference(position, ControlType.kSmartMotion);
	}

	public void magicallyGoToPositionUsingNormalDZ(double position){
		if(position > maxpos1){
			position = maxpos1;
		} else if(position < minpos1){
			position = minpos1;
		}
		currentPos = position;
	}

	public void run(boolean enabled){
		if(enabled){
			pid1.setReference(currentPos, ControlType.kSmartMotion);

			if (mc1.getSensorPosition() <= (dz1 + mc1.getSensorPosition()) && mc1.getSensorPosition() >= (mc1.getSensorPosition() - dz1)){
				combo ++;
			} else {
				combo = 0;
			}

		} else{
			log("run loop is disabled", Severity.WARNING); // ?
		}
		
	}

	}

