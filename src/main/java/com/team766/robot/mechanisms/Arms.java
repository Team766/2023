package com.team766.robot.mechanisms;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.SparkMaxAlternateEncoder;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.SparkMaxRelativeEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;
import com.revrobotics.CANSparkMax;
import com.team766.controllers.PIDController;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController.ControlMode;
import com.team766.hal.wpilib.CANSparkMaxMotorController;
import com.team766.robot.Robot;
import com.team766.hal.EncoderReader;
//This is for the motor that controls the pulley
public class Arms extends Mechanism {
    
    private MotorController firstJoint = RobotProvider.instance.getMotor("arms.firstJoint");
    private CANSparkMax firstJointCANSparkMax = (CANSparkMax)firstJoint;
    private SparkMaxPIDController firstJointPIDController  = firstJointCANSparkMax.getPIDController();
    private SparkMaxAbsoluteEncoder altEncoder = firstJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle);
    private RelativeEncoder mainEncoder = firstJointCANSparkMax.getEncoder();
    private double lastPosition = -1;
    private double maxLocation = 1;
    private double minLocation = 0;

    private MotorController secondJoint = RobotProvider.instance.getMotor("arms.secondJoint");
    private CANSparkMax secondJointTest = (CANSparkMax)secondJoint;
    private SparkMaxPIDController secondJointPID = secondJointTest.getPIDController();
    private SparkMaxAbsoluteEncoder altEncoder2 = secondJointTest.getAbsoluteEncoder(Type.kDutyCycle);
    private RelativeEncoder mainEncoder2 = secondJointTest.getEncoder();
    private double lastPosition2 = -1;
    private double maxLocation2 = 1;
    private double minLocation2 = 0;
/* 
    private MotorController wrist = RobotProvider.instance.getMotor("arms.wrist");
    private CANSparkMax wristJointCSM = (CANSparkMax)wrist;
    private SparkMaxPIDController wristPID = wristJointCSM.getPIDController();
    private SparkMaxAbsoluteEncoder altEncoderWrist = wristJointCSM.getAbsoluteEncoder(Type.kDutyCycle);
    private RelativeEncoder regular = wristJointCSM.getEncoder();
    private double lastPosition3 = -1.0;
    private static double doubleDeadZone = 0.004d;

*/

private static double doubleDeadZone = 0.004d;
    /* 
    private MotorController thirdJoint = RobotProvider.instance.getMotor("arms.thirdJoint");
    private CANSparkMax thirdJointCSM = (CANSparkMax)thirdJoint;
    private SparkMaxPIDController thirdJointPID = thirdJointCSM.getPIDController();
    */

    

    public Arms(){
        /*
        Please dont actually use these pid values rn bc they havent been tested!!!!
        */

        firstJointPIDController.setFeedbackDevice(altEncoder);
        firstJointCANSparkMax.setInverted(false);
        firstJointPIDController.setP(0);
        firstJointPIDController.setI(0);
        firstJointPIDController.setD(0);
        firstJointPIDController.setFF(0.0018999995663762093);
        firstJointPIDController.setSmartMotionMaxVelocity(6000, 0);
        firstJointPIDController.setSmartMotionMinOutputVelocity(0, 0);
        firstJointPIDController.setSmartMotionMaxAccel(3000, 0);
        firstJointPIDController.setOutputRange(-0.25, 0.25);

        secondJointPID.setFeedbackDevice(altEncoder2);
        secondJointPID.setP(0.0);
        secondJointPID.setI(0);
        secondJointPID.setD(0);
        secondJointPID.setFF(0.002099999724328518);
        secondJointPID.setSmartMotionMaxVelocity(6000, 0);
        secondJointPID.setSmartMotionMinOutputVelocity(0, 0);
        secondJointPID.setSmartMotionMaxAccel(3000, 0);
        secondJointPID.setOutputRange(-1, 1);
/* 
        wristPID.setFeedbackDevice(altEncoderWrist);
        wristPID.setP(0);
        wristPID.setI(0);
        wristPID.setD(0);
        wristPID.setFF(0);
        wristPID.setSmartMotionMaxVelocity(6000, 0);
        wristPID.setSmartMotionMinOutputVelocity(0, 0);
        wristPID.setSmartMotionMaxAccel(3000, 0);
        wristPID.setOutputRange(-0.25, 0.25);


        */
        
    }


    //This allows the pulley motor power to be changed, usually manually
    //The magnitude ranges from 0.0-1.0, and sign (positive/negative) determines the direction
    /* 
    public void pid3(double value){
        if(value > maxLocation){
            value = maxLocation;
        } else if( value < minLocation){
            value = minLocation;
        }
        if(lastPosition3 != value) {
            if(wristJointCSM.getAbsoluteEncoder(Type.kDutyCycle).getPosition() > value - doubleDeadZone &&
                wristJointCSM.getAbsoluteEncoder(Type.kDutyCycle).getPosition()< value + doubleDeadZone){
                
                wristPID.setFeedbackDevice(mainEncoder);
                wrist.set((-Math.sin((Math.PI / 88) * firstJoint.getSensorPosition())) * .021);
                lastPosition3 = value;
                log("it worked");
            }else{
                wristPID.setFeedbackDevice(altEncoderWrist);
                wristPID.setReference(value, CANSparkMax.ControlType.kSmartMotion);
                log("it went back in");
            }

        } else {
            wrist.set((-Math.sin((Math.PI / 88) * firstJoint.getSensorPosition())) * .021);
        }
    }
*/
    public void manuallySetArmOnePower(double power) {
        checkContextOwnership();
        firstJoint.set(power);
    }
    // Getter method for getting the first arms encoder distance
    public double getEncoderDistanceOfArmOne() {
        return altEncoder.getPosition();
    }
    // resetting the encoder distance to     zero for use without absolutes
    public void resetEncoders(){
        checkContextOwnership();
        firstJoint.setSensorPosition(0);
        secondJoint.setSensorPosition(0);
    }

	// PID for first arm
    public void pidForArmOne(double value){
        log("" + firstJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle).getPosition());
        if(value > maxLocation){
            value = maxLocation;
        } else if( value < minLocation){
            value = minLocation;
        }
        if(lastPosition != value) {
            if(firstJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle).getPosition() > value - doubleDeadZone &&
                firstJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle).getPosition()< value + doubleDeadZone){
                
                firstJointPIDController.setFeedbackDevice(mainEncoder);
                firstJoint.set((-Math.sin((Math.PI / 88) * firstJoint.getSensorPosition())) * .021);
                lastPosition = value;
                log("it worked");
            }else{
                firstJointPIDController.setFeedbackDevice(altEncoder);
                firstJointPIDController.setReference(value, CANSparkMax.ControlType.kSmartMotion);
                log("it went back in");
            }

        } else {
            firstJoint.set((-Math.sin((Math.PI / 88) * firstJoint.getSensorPosition())) * .021);
        }
        

        


        
    }

	// PID for second arm
    public void pidForArmTwo(double value){
        if(value > maxLocation2){
            value = maxLocation2;
        } else if( value < minLocation2){
            value = minLocation2;
        }
        if(lastPosition2 != value) {
            if(secondJointTest.getAbsoluteEncoder(Type.kDutyCycle).getPosition() > value - doubleDeadZone &&
                secondJointTest.getAbsoluteEncoder(Type.kDutyCycle).getPosition()< value + doubleDeadZone){
                
                secondJointPID.setFeedbackDevice(mainEncoder2);
                secondJoint.set(((-Math.sin((Math.PI / 88)* secondJoint.getSensorPosition())) * .011));
                lastPosition2 = value;
                log("it worked");
            }else{
                secondJointPID.setFeedbackDevice(altEncoder2);
                secondJointPID.setReference(value, CANSparkMax.ControlType.kSmartMotion);
                log("it went back in");
            }

        } else {
            secondJoint.set((-Math.sin((Math.PI / 88)* secondJoint.getSensorPosition())) * .011);
        }
        
        
    }
	
	// resetting time for use with the I in PID.
	
	// getter method for getting the encoder position of arm 2
    public double getEncoderDistanceOfArmTwo(){
        return altEncoder2.getPosition();
    }

	// antigrav
    public void holdArms(){ // Use Encoder Units to Radians in the sine
        firstJoint.set((-Math.sin((Math.PI / 88) * firstJoint.getSensorPosition())) * .021);
        secondJoint.set((-Math.sin((Math.PI / 88)* secondJoint.getSensorPosition())) * .011);
        

    }

	//changing degrees to encoder units for the non absolute encoder
    public double degreesToEU(double angle) {
        return angle * (44.0 / 90);
    }
	
	// manual changing of arm 2.
    public void manuallySetArmTwoPower(double power){
        checkContextOwnership();
        secondJoint.set(power);
    }

    
        
    

    
    }


/* ~~ Code Review ~~
    Use Voltage Control Mode when setting power (refer to CANSparkMaxMotorController.java)

    Maybe use Nicholas's formula for degrees to EU
    "Use break mode" - Ronald te not programmer

 */
