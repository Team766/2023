package com.team766.robot.mechanisms;

import com.team766.controllers.PIDController;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController.ControlMode;
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
	// getting motors from config file
        firstJoint = RobotProvider.instance.getMotor("arms.firstJoint");
        secondJoint = RobotProvider.instance.getMotor("arms.secondJoint");
	    
	// We are able to tune the PID directly from the config file, so this is where we get the PID values set there
        pid = PIDController.loadFromConfig("ArmJoint1");
        pid4arm2 = PIDController.loadFromConfig("ArmJoint2");
        


    }
    //This allows the pulley motor power to be changed, usually manually
    //The magnitude ranges from 0.0-1.0, and sign (positive/negative) determines the direction
    public void setPulleyPower(double power) {
        checkContextOwnership();
        firstJoint.set(power);
    }
    // Getter method for getting the first arms encoder distance
    public double getEncoderDistance() {
        return firstJoint.getSensorPosition();
    }
    // resetting the encoder distance to zero for use without absolutes
    public void resetEncoder(){
        checkContextOwnership();
        firstJoint.setSensorPosition(0);
        secondJoint.setSensorPosition(0);
    }

	// PID for first arm
    public void pidtest(double value){
            pid.setSetpoint(value);
            pid.calculate(firstJoint.getSensorPosition());
            firstJoint.set(pid.getOutput()+0); //add ff to power
            log("PID Output: " + pid.getOutput());

    }
	// PID for second arm
    public void pidForArm2(double height_encoderUnits){
        pid4arm2.setSetpoint(height_encoderUnits);
        pid4arm2.calculate(secondJoint.getSensorPosition());
        secondJoint.set(pid4arm2.getOutput());
        log("PID Output: approx " + pid4arm2.getOutput());
    }
	
	// resetting time for use with the I in PID.
    public void reset(){
        pid.reset();
        pid4arm2.reset();
        
    }
	
	// getter method for getting the encoder position of arm 2
    public double findEU(){
        return secondJoint.getSensorPosition();
    }
	// antigrav
    public void setFf(){ // Use Encoder Units to Radians in the sine
        firstJoint.set((-Math.sin((Math.PI / 88) * firstJoint.getSensorPosition())) * .021);
        secondJoint.set((-Math.sin((Math.PI / 88) * findEU())) * .011);
        log("ff: " + (-Math.sin(Math.PI / 88) * firstJoint.getSensorPosition()) * .021);
    }

    

	//changing degrees to encoder units for the non absolute encoder
    public double degreesToEU(double angle) {
        return angle * (44.0 / 90);
    }
	
	// manual changing of arm 2.
    public void setA2(double set){
        secondJoint.set(set);
    }
}

/* ~~ Code Review ~~
    Use Voltage Control Mode when setting power (refer to CANSparkMaxMotorController.java)

    Maybe use Nicholas's formula for degrees to EU
    "Use break mode" - Ronald te not programmer

 */
