package com.team766.robot.mechanisms;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.SparkMaxAlternateEncoder;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.SparkMaxRelativeEncoder;
import com.revrobotics.CANSparkMax.ControlType;
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
    private SparkMaxAbsoluteEncoder altEncoder1 = firstJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle);

    private MotorController secondJoint = RobotProvider.instance.getMotor("arms.secondJoint");
    private CANSparkMax secondJointCANSparkMax = (CANSparkMax)secondJoint;
    private SparkMaxPIDController secondJointPIDController = secondJointCANSparkMax.getPIDController();
    private SparkMaxAbsoluteEncoder altEncoder2 = secondJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle);
    

    // Non-motor constants
    private static double doubleDeadZone = 5;

    enum ArmState{
        PID, // Moving
        ANTIGRAV // Holding
    };

    private ArmState firstJointState = ArmState.ANTIGRAV;
    private ArmState secondJointState = ArmState.ANTIGRAV;

    private static final double ANTI_GRAV_FIRST_JOINT = 0.02;
    private static final double ANTI_GRAV_SECOND_JOINT = 0.001;
    private static final double ANTI_GRAV_FIRSTSECOND_JOINT = 0.001;

    // We want firstJoint/secondJoint being straight-up to be 0 rel encoder units 
    // and counter-clockwise to be positive.
    // All the following variables are in degrees

    private double firstJointPosition = 0; 
    private double secondJointPosition = 0; 

    private static final double FIRST_JOINT_MAX_LOCATION = 90; 
    private static final double FIRST_JOINT_MIN_LOCATION = -90;
    private static final double SECOND_JOINT_MAX_LOCATION = 90;
    private static final double SECOND_JOINT_MIN_LOCATION = -90;

    public Arms(){
        /*
        Please dont actually use these pid values rn bc they havent been tested!!!!
        */
        
        firstJointCANSparkMax.setInverted(false);
        firstJointPIDController.setP(0);
        firstJointPIDController.setI(0);
        firstJointPIDController.setD(0);
        firstJointPIDController.setFF(0.002499999478459358);
        firstJointPIDController.setSmartMotionMaxVelocity(6000, 0);
        firstJointPIDController.setSmartMotionMinOutputVelocity(0, 0);
        firstJointPIDController.setSmartMotionMaxAccel(3000, 0);
        firstJointPIDController.setOutputRange(-0.25, 0.25);
        altEncoder1.setZeroOffset(0.68);
        firstJointCANSparkMax.setSmartCurrentLimit(15, 40);
        firstJointPIDController.setFeedbackDevice(firstJointCANSparkMax.getEncoder());

        secondJointPIDController.setP(0);
        secondJointPIDController.setI(0);
        secondJointPIDController.setD(0);
        secondJointPIDController.setFF(0.001);
        secondJointPIDController.setSmartMotionMaxVelocity(6000, 0);
        secondJointPIDController.setSmartMotionMinOutputVelocity(0, 0);
        secondJointPIDController.setSmartMotionMaxAccel(3000, 0);
        secondJointPIDController.setOutputRange(-0.5, .5);
        altEncoder2.setZeroOffset(0.62);
        secondJointCANSparkMax.setSmartCurrentLimit(15, 40);
        secondJointPIDController.setFeedbackDevice(secondJointCANSparkMax.getEncoder());
    }


    //This allows the pulley motor power to be changed, usually manually
    //The magnitude ranges from 0.0-1.0, and sign (positive/negative) determines the direction

    // TODO: Is this needed?
    public void addArms(MotorController motor1, MotorController motor2){
        firstJoint = motor1;
        secondJoint = motor2;

        firstJointCANSparkMax = (CANSparkMax)firstJoint;
        secondJointCANSparkMax = (CANSparkMax)secondJoint;
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

    public void resetEncoders(){
        checkContextOwnership();

        // altEncoder1Offset = what is the value of altEncoder1 when firstJoint is vertical
        double firstJointAbsEncoder = altEncoder1.getPosition();
        double altEncoder1Offset = 0.25;
        double firstJointRelEncoder = AbsToEU(firstJointAbsEncoder-altEncoder1Offset);

        // altEncoder2Offset = what is the value of altEncoder2 when secondJoint is colinear w/firstJoint
        double secondJointAbsEncoder = altEncoder2.getPosition();
        double altEncoder2Offset = 0.5;
        double secondJointRelEncoder = AbsToEU(firstJointAbsEncoder-altEncoder1Offset+secondJointAbsEncoder-altEncoder2Offset);

        // set the sensor positions of our rel encoders
        firstJoint.setSensorPosition(firstJointRelEncoder);
        secondJoint.setSensorPosition(secondJointRelEncoder);

        firstJointPosition = EUTodegrees(firstJointRelEncoder);
        secondJointPosition = EUTodegrees(secondJointRelEncoder);
    }

	// PID for first arm
    /**
     * Set PID for the first joint.
     * 
     * @param value desired position in degrees.
     */
    public void pidForArmOne(double value){ // This will be run once
        // log("First Joint Absolute Encoder: " + altEncoder1.getPosition());
        // log("" + firstJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle).getPosition());

        // If value is out of range, then adjust value.
        value = clampValueToRange(value, FIRST_JOINT_MAX_LOCATION, FIRST_JOINT_MIN_LOCATION);

        firstJointPosition = value;
        firstJointPIDController.setReference(degreesToEU(firstJointPosition),ControlType.kSmartMotion,0,getAntiGravFirstJoint());
        firstJointState = ArmState.PID;
    }


	// PID for second arm
    public void pidForArmTwo(double value){
        log("Second Joint Absolute Encoder: " + altEncoder2.getPosition());
        // log("" + firstJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle).getPosition());

        // If value is out of range, then adjust value.
        value = clampValueToRange(value, SECOND_JOINT_MAX_LOCATION, SECOND_JOINT_MIN_LOCATION);

        secondJointPosition = value;
        secondJointPIDController.setReference(degreesToEU(secondJointPosition),ControlType.kSmartMotion,0,getAntiGravSecondJoint());
        secondJointState = ArmState.PID;

    }

	// These next 3 antiGrav aren't used.
    public void antiGravBothJoints(){  
        antiGravFirstJoint();
        antiGravSecondJoint();
    }

    private void antiGravFirstJoint(){
        firstJoint.set(getAntiGravFirstJoint());
        firstJointState = ArmState.ANTIGRAV;
    }

    private void antiGravSecondJoint(){
        secondJoint.set(getAntiGravSecondJoint());
        secondJointState = ArmState.ANTIGRAV;
    }

    // These next 2 antiGravs are used.
    private double betterGetAntiGravFirstJoint(){
        double firstRelEncoderAngle = EUTodegrees(firstJoint.getSensorPosition());
        double secondRelEncoderAngle = EUTodegrees(secondJoint.getSensorPosition());
        double massRatio = 2; //ratio between firstJoint and secondJoint
        double triangleSide1 = 38; // firstJoint length
        double triangleSide2 = 38; // half secondJoint length
        double middleAngle = 180-(secondRelEncoderAngle-firstRelEncoderAngle);
        double triangleSide3 = lawOfCosines(triangleSide1,triangleSide2,middleAngle);
        double firstSecondJointAngle = firstRelEncoderAngle+lawOfSines(triangleSide3,middleAngle,triangleSide2);
        double firstJointAngle = 90-Math.abs(firstRelEncoderAngle);
        return (-1*Math.signum(firstRelEncoderAngle) * Math.cos((Math.PI / 180) * firstJointAngle) * ANTI_GRAV_FIRST_JOINT) + (-1*Math.signum(firstSecondJointAngle)*triangleSide3 * Math.sin((Math.PI / 180)*firstSecondJointAngle) * ANTI_GRAV_FIRSTSECOND_JOINT);
    }

    private double getAntiGravFirstJoint(){
        double firstRelEncoderAngle = EUTodegrees(firstJoint.getSensorPosition());
        double firstJointAngle = 90-Math.abs(firstRelEncoderAngle);
        return Math.signum(firstRelEncoderAngle) * (Math.cos((Math.PI / 180) * firstJointAngle) * ANTI_GRAV_SECOND_JOINT);
    }

    private double getAntiGravSecondJoint(){
        double secondRelEncoderAngle = EUTodegrees(secondJoint.getSensorPosition());
        double secondJointAngle = 90-Math.abs(secondRelEncoderAngle);
        return Math.signum(secondRelEncoderAngle) * (Math.cos((Math.PI / 180) * secondJointAngle) * ANTI_GRAV_SECOND_JOINT);
    }

    @Override
    public void run(){
        // TODO  add rate limiter
        log("Absolute Encoder 1: "+altEncoder1.getPosition());
        log("Relative Encoder 1: "+firstJoint.getSensorPosition());
        log("Absolute Encoder 2: "+altEncoder2.getPosition());
        log("Relative Encoder 2: "+secondJoint.getSensorPosition());

        if (firstJointState == ArmState.ANTIGRAV) {
            firstJointPIDController.setReference(degreesToEU(firstJointPosition), ControlType.kSmartMotion,0,getAntiGravFirstJoint());
        } else {
            firstJointPIDController.setReference(degreesToEU(firstJointPosition),ControlType.kSmartMotion,0,getAntiGravFirstJoint());
            if (Math.abs(altEncoder1.getPosition()-firstJointPosition) <= doubleDeadZone){
                firstJointState = ArmState.ANTIGRAV;
            }
        }
        
        if (secondJointState == ArmState.ANTIGRAV) {
            secondJointPIDController.setReference(degreesToEU(firstJointPosition), ControlType.kSmartMotion,0,getAntiGravSecondJoint());
        } else {
            secondJointPIDController.setReference(degreesToEU(firstJointPosition),ControlType.kSmartMotion,0,getAntiGravSecondJoint());
            if (Math.abs(altEncoder2.getPosition()-secondJointPosition) <= doubleDeadZone){
                secondJointState = ArmState.ANTIGRAV;
            }
        }
    }

    // Helper classes to convert from degrees to EU to absEU
    // TODO: update EU/degree constant for new gearbox 3:5:5

    public double degreesToEU(double angle){
        return angle * (44.0 / 90)*(25.0 / 16.0);
    }
	
    public double EUTodegrees(double EU){
        return EU * (90 / 44.0)*(16.0 / 25.0);
    }

    public double AbsToEU(double abs){
        return abs;
    }

    public double EUToAbs(double EU){
        return EU;
    }

    public double lawOfCosines(double side1, double side2, double angle){ // angle in degrees
        double side3Squared = (Math.pow(side1,2.0)+Math.pow(side2,2.0)-2*side1*side2*Math.cos(Math.toRadians(angle)));
        return Math.sqrt(side3Squared);
    }

    public double lawOfSines(double side1, double angle1, double side2){
        return Math.asin(side2*Math.sin(angle1)/side1);
    }

    private double clampValueToRange(double value, double max, double min) {
        if(value > max){ 
            value = max;
        } else if( value < min){
            value = min;
        }
        return value;
    }

}

/* ~~ Code Review ~~
    Use Voltage Control Mode when setting power (refer to CANSparkMaxMotorController.java)

    Maybe use Nicholas's formula for degrees to EU
    "Use break mode" - Ronald the not programmer

 */
