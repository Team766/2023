package com.team766.robot.mechanisms;

import com.team766.controllers.PIDController;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.EncoderReader;
//This is for the motor that controls the pulley
public class Arms extends Mechanism {
    //This enables the code to interact with the motor that controls the pulley
    private MotorController firstJoint;
    private MotorController secondJoint;
    private EncoderReader firstJointReader;
    private EncoderReader secondJointreader;
    private PIDController pid;
    private PIDController pid4arm2;

    // private EncoderReader pulleyEncoder;

    public Arms() {
        firstJoint = RobotProvider.instance.getMotor("arms.firstJoint");
        secondJoint = RobotProvider.instance.getMotor("arms.secondJoint");
        pid = PIDController.loadFromConfig("ArmJoint1");
        pid4arm2 = PIDController.loadFromConfig("ArmJoint2");
        //(.15,0,0, (-Math.sin((Math.PI / 88) * firstJoint.getSensorPosition()) * .027), -.07, .07, 3);


    }
    //This allows the pulley motor power to be changed
    //The magnitude ranges from 0.0-1.0, and sign (positive/negative) determines the direction
    public void setPulleyPower(double power) {
        checkContextOwnership();
        firstJoint.set(power);
    }

    public double getEncoderDistance() {
        return firstJoint.getSensorPosition();
    }
    public void resetEncoder(){
        checkContextOwnership();
        firstJoint.setSensorPosition(0);
        secondJoint.setSensorPosition(0);
    }
	public void setPosition(double position){
        //checkContextOwnership();
		while(firstJoint.getSensorPosition() != position){
			if(firstJoint.getSensorPosition() < position){
				firstJoint.set(.17);
			} else if(firstJoint.getSensorPosition()> position){
				firstJoint.set(-.17);
			}
			firstJoint.set(0);
		}
	}
    public void pidtest(double value){
        //if(firstJoint.getSensorPosition() > 3240){
            
        //}else{
            pid.setSetpoint(value);
            pid.calculate(firstJoint.getSensorPosition());
            firstJoint.set(pid.getOutput()+0); //add ff to power
            log("PID Output: " + pid.getOutput());
            //log("" + firstJoint.getSensorPosition());
        //}
    }

    public void pidForArm2(double height_encoderUnits){
        pid4arm2.setSetpoint(height_encoderUnits);
        pid4arm2.calculate(secondJoint.getSensorPosition());
        secondJoint.set(pid4arm2.getOutput());
        log("PID Output: approx " + pid4arm2.getOutput());
    }
    public void reset(){
        pid.reset();
        pid4arm2.reset();
        
    }
    public double findEU(){
        return secondJoint.getSensorPosition();
    }
    public void setFf(){ // Use Encoder Units to Radians in the sine
        firstJoint.set((-Math.sin((Math.PI / 88) * firstJoint.getSensorPosition())) * .021);
        log("ff: " + (-Math.sin(Math.PI / 88) * firstJoint.getSensorPosition()) * .021);
    }

    public double degreesToEU(double angle) {
        return angle * (44.0 / 90);
    }

    public void setA2(double set){
        secondJoint.set(set);
    }
}

/* ~~ Code Review ~~
    Make Anti-Grav function better by putting an EU to radians converter inside the sine function
    Use Voltage Control Mode when setting power (refer to CANSparkMaxMotorController.java)

    Maybe use Nicholas's formula for degrees to EU
    "Use break mode" - Ronald te not programmer

 */