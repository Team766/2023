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

    // private EncoderReader pulleyEncoder;

    public Arms() {
        firstJoint = RobotProvider.instance.getMotor("arms.firstJoint");
        secondJoint = RobotProvider.instance.getMotor("arms.secondJoint");
        pid = new PIDController(.001,0,0, (-Math.sin((Math.PI / 88) * firstJoint.getSensorPosition()) * .027), -.07, .07, 3);


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
    public void pidtest(){
        //if(firstJoint.getSensorPosition() > 3240){
            
        //}else{
            pid.setSetpoint(00);
            pid.calculate(firstJoint.getSensorPosition());
            firstJoint.set(pid.getOutput()+0);
            log("PID Output: " + pid.getOutput());
            //log("" + firstJoint.getSensorPosition());
        //}
    }
    public void reset(){
        pid.reset();
    }
    public void setFf(){
        firstJoint.set((-Math.sin(Math.PI / 88) * firstJoint.getSensorPosition()) * .027);
    }
}