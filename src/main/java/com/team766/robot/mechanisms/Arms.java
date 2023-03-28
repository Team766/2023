package com.team766.robot.mechanisms;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.SparkMaxAlternateEncoder;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.SparkMaxRelativeEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;
import com.revrobotics.CANSparkMax;
import com.team766.config.ConfigFileReader;
import com.team766.controllers.PIDController;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController.ControlMode;
import com.team766.hal.wpilib.CANSparkMaxMotorController;
import com.team766.library.RateLimiter;
import com.team766.library.ValueProvider;
import com.team766.robot.Robot;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import com.team766.hal.EncoderReader;
//This is for the motor that controls the pulley
public class Arms extends Mechanism {

    
    private ValueProvider<Double> ANTI_GRAV_FIRST_JOINT = ConfigFileReader.getInstance().getDouble("arms.antiGravFirstJoint");
    private ValueProvider<Double> ANTI_GRAV_SECOND_JOINT = ConfigFileReader.getInstance().getDouble("arms.antiGravSecondJoint");

    private MotorController firstJoint = RobotProvider.instance.getMotor("arms.firstJoint");
    private CANSparkMax firstJointCANSparkMax = (CANSparkMax)firstJoint;
    private CANSparkMaxMotorController firstJointCSMMC = (CANSparkMaxMotorController)firstJoint;
    private SparkMaxPIDController firstJointPIDController  = firstJointCANSparkMax.getPIDController();
    private SparkMaxAbsoluteEncoder altEncoder1 = firstJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle);
    private RelativeEncoder mainEncoder = firstJointCANSparkMax.getEncoder();
    private double lastPosition = -1;
    private double maxLocation = 1;
    private double minLocation = 0;

    private MotorController secondJoint = RobotProvider.instance.getMotor("arms.secondJoint");
    private CANSparkMax secondJointCANSparkMax = (CANSparkMax)secondJoint;
    private CANSparkMaxMotorController secondJoinsCSMMC = (CANSparkMaxMotorController)secondJoint;
    private SparkMaxPIDController secondJointPID = secondJointCANSparkMax.getPIDController();
    private SparkMaxAbsoluteEncoder altEncoder2 = secondJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle);
    private RelativeEncoder mainEncoder2 = secondJointCANSparkMax.getEncoder();
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

    private static double doubleDeadZone = 3;
    /* 
    private MotorController thirdJoint = RobotProvider.instance.getMotor("arms.thirdJoint");
    private CANSparkMax thirdJointCSM = (CANSparkMax)thirdJoint;
    private SparkMaxPIDController thirdJointPID = thirdJointCSM.getPIDController();
    */

    enum ArmState{
        MOVING,
        HOLDING
    };

    public ArmState firstJointState = ArmState.HOLDING;
    public ArmState secondJointState = ArmState.HOLDING;
    
    

    public Arms(){
        /*
        Please dont actually use these pid values rn bc they havent been tested!!!!
        */
        loggerCategory = Category.OPERATOR_INTERFACE;

        firstJointPIDController.setFeedbackDevice(altEncoder1);
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
        secondJointPID.setP(0.009999999776482582);
        secondJointPID.setI(0);
        secondJointPID.setD(0.00019999999494757503);
        secondJointPID.setFF(0.010999997146427631);
        secondJointPID.setSmartMotionMaxVelocity(6000, 0);
        secondJointPID.setSmartMotionMinOutputVelocity(0, 0);
        secondJointPID.setSmartMotionMaxAccel(3000, 0);
        secondJointPID.setOutputRange(-1, 1);

        
    }


    //This allows the pulley motor power to be changed, usually manually
    //The magnitude ranges from 0.0-1.0, and sign (positive/negative) determines the direction


    public void resetEncodersReal(){
        firstJoint.setSensorPosition(0);
        secondJoint.setSensorPosition(0);
        }    
    
    public void manuallySetArmOnePower(double power) {
        checkContextOwnership();
        firstJoint.set(power);
    }

    // manual changing of arm 2.
    public void manuallySetArmTwoPower(double power){
        checkContextOwnership();
        secondJoint.set(power);
    }

    // Getter method for getting the first arms encoder distance
    
    public double getEncoderDistanceOfArmOne() {
        return altEncoder1.getPosition();
    }
    // resetting the encoder distance to     zero for use without absolutes
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

        lastPosition = EUTodegrees(firstJointRelEncoder);
        lastPosition2 = EUTodegrees(secondJointRelEncoder);
    }

	//PID for first arm
    public void pidForArmOne(double value){
        checkContextOwnership();
        firstJointPIDController.setFeedbackDevice(mainEncoder);
        // log("" + firstJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle).getPosition());
        if(value > maxLocation){
            value = maxLocation;
        } else if( value < minLocation){
            value = minLocation;
        }

        if(lastPosition != value) {
            if(firstJoint.getSensorPosition() > value - doubleDeadZone &&
                firstJoint.getSensorPosition()< value + doubleDeadZone){
                
                
                lastPosition = value;
                log("it worked");
                firstJointState = ArmState.HOLDING;
            }else{
                firstJointPIDController.setFeedbackDevice(altEncoder1);
                firstJointPIDController.setReference(value, CANSparkMax.ControlType.kSmartMotion);
                log("it went back in");
                firstJointState = ArmState.MOVING;
            }

        }
    }

	// PID for second arm
    public void pidForArmTwo(double value){
        checkContextOwnership();

        if(value > maxLocation2){
            value = maxLocation2;
        } else if( value < minLocation2){
            value = minLocation2;
        }
        if(lastPosition2 != value) {
            if(secondJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle).getPosition() > value - doubleDeadZone &&
                secondJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle).getPosition()< value + doubleDeadZone){
                
                secondJointPID.setFeedbackDevice(mainEncoder2);
                lastPosition2 = value;
                secondJointState = ArmState.HOLDING;
                log("it worked");
            }else{
                secondJointPID.setFeedbackDevice(altEncoder2);
                secondJointPID.setReference(value, CANSparkMax.ControlType.kSmartMotion);
                secondJointState = ArmState.MOVING;
                log("it went back in");
            }
        }
    }

    public void antiGravFirstJoint(){
        firstJoint.set(getAntiGravFirstJoint());
    }

    public void antiGravSecondJoint(){
        secondJoint.set(getAntiGravSecondJoint());
    }

    public double getAntiGravFirstJoint(){
        double firstRelEncoderAngle = EUTodegrees(firstJoint.getSensorPosition());
        double firstJointAngle = 90-Math.abs(firstRelEncoderAngle);
        double power = -1* Math.signum(firstRelEncoderAngle) * (Math.cos((Math.PI / 180) * firstJointAngle) * ANTI_GRAV_FIRST_JOINT.valueOr(0.0));
        // log("AntiGravFirstJoint: "+power);
        return power;
    }

    public double getAntiGravSecondJoint(){
        double secondRelEncoderAngle = EUTodegrees(secondJoint.getSensorPosition());
        double secondJointAngle = 90-Math.abs(secondRelEncoderAngle);
        double power = -1* Math.signum(secondRelEncoderAngle) * (Math.cos((Math.PI / 180) * secondJointAngle) * ANTI_GRAV_SECOND_JOINT.valueOr(0.0));
        // log("AntiGravSecondJoint: "+power);
        return power;
    }
	
	// resetting time for use with the I in PID.
	
	// getter method for getting the encoder position of arm 2
    public double getEncoderDistanceOfArmTwo(){
        return altEncoder2.getPosition();
    }

	// antigrav
    // public void holdArms(){ // Use Encoder Units to Radians in the sine
    //     firstJoint.set((-Math.sin((Math.PI / 88) * firstJoint.getSensorPosition())) * .021);
    //     secondJoint.set((-Math.sin((Math.PI / 88)* secondJoint.getSensorPosition())) * .011);
        

    // }

	//changing degrees to encoder units for the non absolute encoder
    // 1 abs = 360 degrees

    public double degreesToEU(double angle) {
        return angle * (44.0 / 90) * (25.0 / 16);
    }

    public double AbsToEU(double abs) {
        return degreesToEU(360*abs);
    }

    public double EUTodegrees(double eu) {
        return eu * (90 / 44.0) * (16.0 / 25);
    }

    public double EUtoabs(double eu) {
        return EUTodegrees(eu)/360.0;
    }
}


/* ~~ Code Review ~~
    Use Voltage Control Mode when setting power (refer to CANSparkMaxMotorController.java)

    Maybe use Nicholas's formula for degrees to EU
    "Use break mode" - Ronald te not programmer

 */
