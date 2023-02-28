package com.team766.robot.mechanisms;

import com.revrobotics.CANSparkMaxLowLevel;
import com.team766.controllers.PIDController;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController.ControlMode;
import com.team766.hal.wpilib.CANSparkMaxMotorController;
import com.team766.hal.EncoderReader;
//This is for the motor that controls the pulley
public class Arms extends Mechanism {
    //This enables the code to interact with the motor that controls the pulley
    private MotorController firstJoint;
    private MotorController secondJoint;
    private EncoderReader firstJointReader;
    private EncoderReader secondJointreader;

    private CANSparkMaxMotorController firstJointEx;
    private CANSparkMaxMotorController secondJointEx;
 
    // private EncoderReader pulleyEncoder;

    public Arms() {
	// getting motors from config file
        firstJoint = RobotProvider.instance.getMotor("arms.firstJoint");
        secondJoint = RobotProvider.instance.getMotor("arms.secondJoint");

        //MANUALLY CASTING THE MOTORS AS A CANSPARKMAXMOTORCONTROLLER SO THAT WE CAN USE CANSPARKMAX PID
        firstJointEx = (CANSparkMaxMotorController)firstJoint;
        secondJointEx = (CANSparkMaxMotorController)secondJoint;

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
        firstJointEx.set(ControlMode.Position, value);
        log(" he" + value );
        
    }
	// PID for second arm
    public void pidForArm2(double height_encoderUnits){
        secondJointEx.set(ControlMode.Position, height_encoderUnits);
    }
	
	// resetting time for use with the I in PID.
	
	// getter method for getting the encoder position of arm 2
    public double findEU(){
        return secondJoint.getSensorPosition();
    }
	// antigrav
    public void setFfA(){ // Use Encoder Units to Radians in the sine
        firstJoint.set((-Math.sin((Math.PI / 88) * firstJoint.getSensorPosition())) * .021);
        
        log("ff: " + (-Math.sin(Math.PI / 88) * firstJoint.getSensorPosition()) * .021);
    }

    public void setFfB(){
        secondJoint.set((-Math.sin((Math.PI / 88) * findEU())) * .011);
    }

    

	//changing degrees to encoder units for the non absolute encoder
    public double degreesToEU(double angle) {
        return angle * (44.0 / 90);
    }
	
	// manual changing of arm 2.
    public void setA2(double set){
        secondJoint.set(set);
    }

    public boolean checkLimits(double a1_pos, double a2_pos){
        if(a1_pos < 40 && a1_pos > -30 && a2_pos > -40 && a2_pos < 40 ){
            return true;
        } else {
            firstJoint.set((-Math.sin((Math.PI / 88) * firstJoint.getSensorPosition())) * .021);
            secondJoint.set((-Math.sin((Math.PI / 88) * findEU())) * .011);
            return false;
        }
        
    }

    
    }


/* ~~ Code Review ~~
    Use Voltage Control Mode when setting power (refer to CANSparkMaxMotorController.java)

    Maybe use Nicholas's formula for degrees to EU
    "Use break mode" - Ronald te not programmer

 */
