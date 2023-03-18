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


    private static double doubleDeadZone = 0.004d;

    enum ArmState{
        PID,
        ANTIGRAV
    };

    private ArmState firstJointState = ArmState.ANTIGRAV;
    private ArmState secondJointState = ArmState.ANTIGRAV;

    private double antiGravFirstJoint = 0.02;
    private double antiGravSecondJoint = 0.001;

    private double firstArmPosition = 0;
    private double secondArmPosition;

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
        firstJointPIDController.setFF(0.002499999478459358);
        firstJointPIDController.setSmartMotionMaxVelocity(6000, 0);
        firstJointPIDController.setSmartMotionMinOutputVelocity(0, 0);
        firstJointPIDController.setSmartMotionMaxAccel(3000, 0);
        firstJointPIDController.setOutputRange(-0.25, 0.25);
        altEncoder.setZeroOffset(0.68);
        firstJointCANSparkMax.setSmartCurrentLimit(15, 40);

        secondJointPID.setFeedbackDevice(altEncoder2);
        secondJointPID.setP(0);
        secondJointPID.setI(0);
        secondJointPID.setD(0);
        secondJointPID.setFF(0.001);
        secondJointPID.setSmartMotionMaxVelocity(6000, 0);
        secondJointPID.setSmartMotionMinOutputVelocity(0, 0);
        secondJointPID.setSmartMotionMaxAccel(3000, 0);
        secondJointPID.setOutputRange(-.5, .5);
        altEncoder2.setZeroOffset(0.62);
        //secondJointTest.setSmartCurrentLimit(15, 40);




        
        
    }


    //This allows the pulley motor power to be changed, usually manually
    //The magnitude ranges from 0.0-1.0, and sign (positive/negative) determines the direction

    public void addArms(MotorController motor1, MotorController motor2){
        firstJoint = motor1;
        secondJoint = motor2;

        firstJointCANSparkMax = (CANSparkMax)firstJoint;
        secondJointTest = (CANSparkMax)secondJoint;
    }

    // manual changing of arm 1
    public void manuallySetArmOnePower(double power){
        checkContextOwnership();
        firstJoint.set(power);
    }

    // manual changing of arm 2.
    public void manuallySetArmTwoPower(double power){
        checkContextOwnership();
        secondJoint.set(power);
    }
    // Getter method for getting the first arms encoder distance
    public double getEncoderDistanceOfArmOne(){
        return firstJoint.getSensorPosition();
    }
    // resetting the encoder distance to zero for use without absolutes
    public void resetEncoders(){
        checkContextOwnership();
        // anticlockwise is positive for both joints
        // first absolute encoder: 0 is horizontal (right), up is 0.25
        // second absolute encoder: 0 is down, right is 0.25, up is 0.5 
        // relative encoders: upward (normal) is 0 degrees, left is 90, right is -90
        double firstAbsEncoderAngleRelativeToNormal = 360 * (altEncoder.getPosition() - 0.25);
        double secondAbsEncoderAngleRelativeFirstJoint = 360 * (altEncoder2.getPosition() - 0.5);
        double secondAbsEncoderAngleRelativeToNormal = secondAbsEncoderAngleRelativeFirstJoint + firstAbsEncoderAngleRelativeToNormal;
        firstJoint.setSensorPosition(degreesToEU(firstAbsEncoderAngleRelativeToNormal));
        secondJoint.setSensorPosition(degreesToEU(secondAbsEncoderAngleRelativeToNormal));
    }

	// PID for first arm
    public void pidForArmOne(double value){
        log("First Joint Absolute Encoder: " + altEncoder.getPosition());
        // log("" + firstJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle).getPosition());

        // If value is out of range, then adjust value.
        if(value > maxLocation){ // TODO: each arm should have different min/max locations / positions
            value = maxLocation;
        } else if( value < minLocation){
            value = minLocation;
        }

        if(lastPosition != value) { // TODO: should have arm-independent lastPosition
            firstArmPosition = value;
            firstJointState = ArmState.PID;
        } 
    }

	// PID for second arm
    public void pidForArmTwo(double value){
        log("First Joint Absolute Encoder: " + altEncoder.getPosition());
        // log("" + firstJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle).getPosition());

        // If value is out of range, then adjust value.
        if(value > maxLocation){
            value = maxLocation;
        } else if( value < minLocation){
            value = minLocation;
        }

        if(lastPosition != value) {
            secondArmPosition = value;
            secondJointState = ArmState.PID;
        } 
    }
	
	// resetting time for use with the I in PID.
	
	// getter method for getting the encoder position of arm 2
    // public double getEncoderDistanceOfArmTwo(){
    //     return secondJoint.getSensorPosition();
    // }

	// antigrav
    public void antiGravBothJoints(){  
        antiGravFirstJoint();
        antiGravSecondJoint();
    }

    private void antiGravFirstJoint(){   // TODO: should set state to antigrav
        firstJoint.set(getAntiGravFirstJoint());
    }

    private void antiGravSecondJoint(){
        secondJoint.set(getAntiGravSecondJoint());
    }

    private double getAntiGravFirstJoint(){
        double firstRelEncoderAngle = EUTodegrees(firstJoint.getSensorPosition());
        double firstJointAngle = 90-Math.abs(firstRelEncoderAngle);
        return (Math.cos((Math.PI / 180) * firstJointAngle) * antiGravFirstJoint);
    }

    private double getAntiGravSecondJoint(){
        double secondRelEncoderAngle = EUTodegrees(secondJoint.getSensorPosition());
        double secondJointAngle = 90-Math.abs(secondRelEncoderAngle);
        return (Math.cos((Math.PI / 180) * secondJointAngle) * antiGravSecondJoint);
    }

	
    

    @Override
    public void run(){
        // TODO  add rate limiter

        if (firstJointState == ArmState.ANTIGRAV) {
            antiGravFirstJoint();
        } else {
            // PID
            //firstJointPIDController.setFF(getAntiGravFirstJoint());
            if (Math.abs(altEncoder.getPosition()-firstArmPosition) <= doubleDeadZone){
                firstJointState = ArmState.ANTIGRAV;
            }
        }
        
        if (secondJointState == ArmState.ANTIGRAV) {
            antiGravSecondJoint();
        } else {
            // PID
            //firstJointPIDController.setFF(getAntiGravSecondJoint());
            if (Math.abs(altEncoder2.getPosition()-secondArmPosition) <= doubleDeadZone){
                secondJointState = ArmState.ANTIGRAV;
            }
        }
    }

    //
    // helpers
    //
    // changing degrees to encoder units for the non absolute encoder

    // TODO: update EU/degree constant for new gearbox 3:5:5

    public static double degreesToEU(double angle){
        return angle * (44.0 / 90);
    }
	
    public static double EUTodegrees(double EU){
        return EU * (90 / 44.0);
    }

}

/* ~~ Code Review ~~
    Use Voltage Control Mode when setting power (refer to CANSparkMaxMotorController.java)

    Maybe use Nicholas's formula for degrees to EU
    "Use break mode" - Ronald the not programmer

 */
