package com.team766.robot.mechanisms;

import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.team766.controllers.*;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.library.RateLimiter;
import com.team766.library.ValueProvider;
//import com.team766.logging.Category;
//This is for the motor that controls the pulley

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class newArms extends Mechanism {
	CSMMCPID arm1;
	CSMMCPID arm2;
	public newArms(){
		try{
			arm1 = new CSMMCPID("newArms.arm1");
		}catch (Exception e){
			e.printStackTrace();
		}
		
		arm1.setPIDF(0, 0, 0, 0);
		arm1.setOutputRange(1, 1);
		arm1.setMinMaxLocation(0, 0);
		arm1.setDeadzone(2);
		arm1.setMaxAccel(0);
		arm1.setMaxVel(2);
		arm1.setMotorMode(NeutralMode.Brake);
		arm1.setAntigravConstant(0);
		try{
			arm2 = new CSMMCPID("newArms.arm2");	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		arm2.setPIDF(0, 0, 0, 0);
		arm2.setOutputRange(1, 1);
		arm2.setMinMaxLocation(0, 0);
		arm2.setDeadzone(2);
		arm2.setMaxAccel(0);
		arm2.setMaxVel(2);
		arm2.setMotorMode(NeutralMode.Brake);
		arm2.setAntigravConstant(0);
		
	}

	public void go1(double position){
		arm1.setPosition(position);
	}
	public void go2(double position){
		arm2.setPosition(position);
	}
	public void goBoth(double position1, double position2){
		arm1.setPosition(position1);
		arm2.setPosition(position2);
	}

}