package com.team766.robot.mechanisms;

import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.library.RateLimiter;
import com.team766.library.ValueProvider;
import com.team766.logging.Category;
//This is for the motor that controls the pulley

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Arms extends Mechanism {
    /*
     * This defines the motors and casts them to CanSparkMaxs (CSMs) so we can  use REV Robotics PID SmartMotion. 
     * Next, it also defines the absolute encoder.
     */
    private MotorController firstJoint = RobotProvider.instance.getMotor("arms.firstJoint");
    private CANSparkMax firstJointCANSparkMax = (CANSparkMax)firstJoint;
    private SparkMaxPIDController firstJointPIDController  = firstJointCANSparkMax.getPIDController();
    private SparkMaxAbsoluteEncoder altEncoder1 = firstJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle);

    private MotorController secondJoint = RobotProvider.instance.getMotor("arms.secondJoint");
    private CANSparkMax secondJointCANSparkMax = (CANSparkMax)secondJoint;
    private SparkMaxPIDController secondJointPIDController = secondJointCANSparkMax.getPIDController();
    private SparkMaxAbsoluteEncoder altEncoder2 = secondJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle);
	
    // Non-motor constants
    // This is the deadzone, so that the arm(s) don't oscillate. For example, a value of 5 means a 5 relitive encoder unit deadzone in each direction.
    private static final double doubleDeadZone = 2;

    // We want firstJoint/secondJoint being straight-up to be 0 rel encoder units 
    // and counter-clockwise to be positive.
    // All the following variables are in degrees
    private double firstJointPosition = 0; 
    private double secondJointPosition = 0; 
	private double firstJointCombo = 0;
	private double secondJointCombo = 0;

    // TODO: this offset is to factor in the difference between the
    //       "zero" for alt encoder and the "zero" when we use degrees
    //       Offset tuning should be done above in `altEncoder1.setZeroOffset`
    private static final double altEncoder1Offset = 0.25;
    private static final double altEncoder2Offset = 0.0;

    // This sets the maximum locations so we can use them in code to make sure the arm joints dont go past there.
    private static final double FIRST_JOINT_MAX_LOCATION = 27.3;
    private static final double FIRST_JOINT_MIN_LOCATION = -45;
    private static final double SECOND_JOINT_MAX_LOCATION = 45;
    private static final double SECOND_JOINT_MIN_LOCATION = -160;

    private RateLimiter runRateLimiter = new RateLimiter(0.05);

    enum ArmState {
        PID,
        ANTIGRAV,
        OFF
    }

    boolean jointOneCanContinue = false;
    public boolean stowed = false;

    ArmState theStateOf1 = ArmState.ANTIGRAV;
    ArmState theStateOf2 = ArmState.ANTIGRAV;


    private ArmsAntiGrav antiGrav;

    public Arms() {
		loggerCategory = Category.MECHANISMS;

        firstJoint.setNeutralMode(NeutralMode.Brake);
        secondJoint.setNeutralMode(NeutralMode.Brake);

        // PID Constants
		ValueProvider<Double> firstJointP = ConfigFileReader.getInstance().getDouble("arms.firstJointP");
		ValueProvider<Double> firstJointI = ConfigFileReader.getInstance().getDouble("arms.firstJointI");
		ValueProvider<Double> firstJointD = ConfigFileReader.getInstance().getDouble("arms.firstJointD");
		ValueProvider<Double> firstJointFF = ConfigFileReader.getInstance().getDouble("arms.firstJointFF");

		firstJointPIDController.setP(firstJointP.valueOr(0.0006));
        firstJointPIDController.setI(firstJointI.valueOr(0.0));
        firstJointPIDController.setD(firstJointD.valueOr(0.0));
        // FF was 0.002
        firstJointPIDController.setFF(firstJointFF.valueOr(0.001));
        // firstJointPIDController.setSmartMotionAllowedClosedLoopError(3, 0);

        // More PID constants
		ValueProvider<Double> secondJointP = ConfigFileReader.getInstance().getDouble("arms.secondJointP");
		ValueProvider<Double> secondJointI = ConfigFileReader.getInstance().getDouble("arms.secondJointI");
		ValueProvider<Double> secondJointD = ConfigFileReader.getInstance().getDouble("arms.secondJointD");
		ValueProvider<Double> secondJointFF = ConfigFileReader.getInstance().getDouble("arms.secondJointFF");
		
        // TODO: use secondJointO/I/D/FF.valueOr
        secondJointPIDController.setP(0.0005);
        secondJointPIDController.setI(secondJointI.valueOr(0.0));
        // D was 0.00001
        secondJointPIDController.setD(0.0000);
        // FF was 0.00109
        secondJointPIDController.setFF(0.0008);

        // These next things deal a lot with the PID SmartMotion
        firstJointCANSparkMax.setInverted(false);
        // TODO : consider decrease velocity instead of decreasing power
        firstJointPIDController.setSmartMotionMaxVelocity(4000, 0);
        firstJointPIDController.setSmartMotionMinOutputVelocity(0, 0);
        firstJointPIDController.setSmartMotionMaxAccel(3000, 0);
        // firstJointPIDController.setOutputRange(-0.75, 0.75);
        firstJointPIDController.setOutputRange(-0.65, 0.65);
        firstJointCANSparkMax.setSmartCurrentLimit(40);
        // Do not use setSmartMotionAllowedClosedLoopError(5, 0) unless it is safe to test without destorying anything

        // These too
        secondJointPIDController.setSmartMotionMaxVelocity(4000, 0);
        secondJointPIDController.setSmartMotionMinOutputVelocity(0, 0);
        secondJointPIDController.setSmartMotionMaxAccel(3000, 0);
        secondJointPIDController.setOutputRange(-1, 1);
        secondJointCANSparkMax.setSmartCurrentLimit(40);

        // This resets the degrees and stuff so that we dont have to have the arm at certain positions to reset...
		firstJointPIDController.setFeedbackDevice(firstJointCANSparkMax.getEncoder());
		secondJointPIDController.setFeedbackDevice(secondJointCANSparkMax.getEncoder());
        
        // absolute encoder zero offsets are set to positions which will never be used
        // to avoid wrap-around errors
        // first joint zero is horizontal parallel to ground
        // second joint zero is when the relative angle is 0 degrees from the first arm segment
		altEncoder1.setZeroOffset(0.64);    // TODO: these need tweaking from altEncoder1Offset
		altEncoder2.setZeroOffset(0.19); //Old values: 0.807, 0.446
        altEncoder2.setPositionConversionFactor(0.975); // 0.89

        antiGrav = new ArmsAntiGrav(firstJoint, secondJoint);

        // We only want to resetEncoders after configs are loaded, offsets are set, etc
        resetFirstEncoders();
        resetSecondEncoders();
    }

    // This allows the pulley motor power to be changed, usually manually
    // The magnitude ranges from 0.0-1.0, and sign (positive/negative) determines the direction

    // manual changing of arm 1
    public void manuallySetArmOnePower(double power) {
        checkContextOwnership();
        firstJoint.set(power);
    }

    // manual changing of arm 2.
    public void manuallySetArmTwoPower(double power) {
        checkContextOwnership();
        secondJoint.set(power);
    }

    public void coast(){
        firstJoint.setNeutralMode(NeutralMode.Coast);
        secondJoint.setNeutralMode(NeutralMode.Coast);
    }

    public void brake(){
        firstJoint.setNeutralMode(NeutralMode.Brake);
        secondJoint.setNeutralMode(NeutralMode.Brake);
    }
    public double nudgeArm2up(){
        return (ArmsUtil.EUTodegrees(secondJoint.getSensorPosition()) +1);
    }

    public double nudgeArm2down(){
        return (ArmsUtil.EUTodegrees(secondJoint.getSensorPosition()) -1);
    }

    public double getSecondJointPosition() {
        return ArmsUtil.EUTodegrees(secondJoint.getSensorPosition());
    }

    // Resets encoders
    public void resetFirstEncoders() {
        checkContextOwnership();

        // altEncoder1Offset = what is the value of altEncoder1 when firstJoint is vertical
        double firstJointAbsEncoder = Math.IEEEremainder(altEncoder1.getPosition() - altEncoder1Offset, 1.0);
        double firstJointRelEncoder = ArmsUtil.AbsToEU(firstJointAbsEncoder);

        // set the sensor positions and setpoint of our rel encoders
        firstJoint.setSensorPosition(firstJointRelEncoder);
        log("Reset Encoder 1 (in degrees): "+ArmsUtil.EUTodegrees(firstJointRelEncoder));
        firstJointPosition = ArmsUtil.EUTodegrees(firstJointRelEncoder);
    }

    public void resetSecondEncoders() {
        checkContextOwnership();

        // altEncoder1Offset = what is the value of altEncoder1 when firstJoint is vertical
        // altEncoder2Offset = what is the value of altEncoder2 when secondJoint is colinear w/firstJoint

        double firstJointAbsEncoder =
            Math.IEEEremainder(altEncoder1.getPosition() - altEncoder1Offset, 1.0);
        double secondJointAbsEncoder =
            Math.IEEEremainder(altEncoder2.getPosition() - altEncoder2Offset, 1.0);
        double secondJointRelEncoder = ArmsUtil.AbsToEU(
            firstJointAbsEncoder + secondJointAbsEncoder);

        // set the sensor positions and setpoint of our rel encoders
        secondJoint.setSensorPosition(secondJointRelEncoder);
        log("Reset Encoder 2 (in degrees): "+ArmsUtil.EUTodegrees(secondJointRelEncoder));
        secondJointPosition = ArmsUtil.EUTodegrees(secondJointRelEncoder);
    }

    public void armStop(){
        brake();
        theStateOf1 = ArmState.OFF;
        theStateOf2 = ArmState.OFF;
    }

	//  for first arm
    /**
     * Set PID for the first joint.
     * 
     * @param value desired position in degrees.
     */
    public void pidForArmOne(double value) {
        // This will be run once
        // log("First Joint Absolute Encoder: " + altEncoder1.getPosition());
        // log("" + firstJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle).getPosition());

        // If value is out of range, then adjust value.
        value = ArmsUtil.clampValueToRange(value, FIRST_JOINT_MIN_LOCATION, FIRST_JOINT_MAX_LOCATION);

        firstJointPosition = value;
        // if(Math.abs(EUTodegrees(firstJoint.getSensorPosition() )))
        firstJointPIDController.setReference(ArmsUtil.degreesToEU(firstJointPosition),
            ControlType.kSmartMotion,
            0,
            antiGrav.getFirstJointPower());
        theStateOf1 = ArmState.PID;
        firstJointCombo = 0;
          
    }

	// PID for second arm
    public void pidForArmTwo(double value) {
        // log("Second Joint Absolute Encoder: " + altEncoder2.getPosition());
        // log("" + firstJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle).getPosition());

        // If value is out of range, then adjust value.

        value = ArmsUtil.clampValueToRange(value, SECOND_JOINT_MIN_LOCATION, SECOND_JOINT_MAX_LOCATION);

        secondJointPosition = value;
        secondJointPIDController.setReference(
            ArmsUtil.degreesToEU(secondJointPosition),
            ControlType.kSmartMotion,
            0,
            antiGrav.getSecondJointPower());
        theStateOf2 = ArmState.PID;
        secondJointCombo = 0;
    
    }

    // Use these for manual pid based angle increment/decrement
    public void alterArmOneAngle(double degrees) {
        pidForArmOne(firstJointPosition + degrees);
    }

    public void alterArmTwoAngle(double degrees) {
        pidForArmTwo(firstJointPosition + degrees);
    }

    // PID For arm one

    // public void checkJointTwo(double value){
    //     if((value + doubleDeadZone > secondJoint.getSensorPosition() && value - doubleDeadZone < secondJoint.getSensorPosition())){
    //         jointOneCanContinue = true;
    //     }else{
    //         jointOneCanContinue = false;
    //     }
    // }

    //This is our portion with antigrav
	// These next 3 antiGrav aren't used.
    public void antiGravBothJoints(){
        antiGravFirstJoint();
        antiGravSecondJoint();
    }

    public void antiGravFirstJoint() {
        antiGrav.updateFirstJoint();
        theStateOf1 = ArmState.ANTIGRAV;
    }

    public void antiGravSecondJoint() {
        antiGrav.updateSecondJoint();
        theStateOf2 = ArmState.ANTIGRAV;
    }

    public void logs(){
        // log("E1: " + ArmsUtil.EUTodegrees(firstJoint.getSensorPosition()));
        // log("E2: " + ArmsUtil.EUTodegrees(secondJoint.getSensorPosition()));
        // log("AE1: " + altEncoder1.getPosition());
        // log("AE2: " + altEncoder2.getPosition());
        SmartDashboard.putNumber("Joint 1 Motor Encoder", ArmsUtil.EUTodegrees(firstJoint.getSensorPosition()));
        SmartDashboard.putNumber("Joint 2 Motor Encoder", ArmsUtil.EUTodegrees(secondJoint.getSensorPosition()));
        SmartDashboard.putNumber("Joint 1 Abs Encoder", 360 * Math.IEEEremainder(altEncoder1.getPosition() - altEncoder1Offset, 1.0));
        SmartDashboard.putNumber("Joint 2 Abs Encoder", 360 * Math.IEEEremainder(altEncoder2.getPosition() - altEncoder2Offset, 1.0));

    }
	
    @Override
    public void run() {
		if(!runRateLimiter.next()) return;
        
        IdleMode idleMode = ((CANSparkMax) secondJoint).getIdleMode();
        SmartDashboard.putString("Idle Mode", (idleMode != null) ? idleMode.toString(): "null");
        if (theStateOf1 == ArmState.PID || theStateOf2 == ArmState.PID) {
            log("First Joint Absolute Encoder: " + altEncoder1.getPosition());
            log("Second Joint Absolute Encoder: " + altEncoder2.getPosition());
            // log("First Joint Relative Encoder: " + firstJoint.getSensorPosition());
            // log("Second Joint Relative Encoder: " + secondJoint.getSensorPosition());
            // log("First Joint Difference: " + (EUTodegrees(firstJoint.getSensorPosition())-firstJointPosition));
            // log("Second Joint Difference: " + (EUTodegrees(secondJoint.getSensorPosition())-secondJointPosition));
            log("Degrees Joint 1: "+ ArmsUtil.EUTodegrees(firstJoint.getSensorPosition()));
            log("Degrees Joint 2: "+ ArmsUtil.EUTodegrees(secondJoint.getSensorPosition()));
            log("First Joint State: " + theStateOf1);
            log("Second Joint State: " + theStateOf2);
            log("First Joint Combo: " + firstJointCombo);
            log("Second Joint Combo: " + secondJointCombo);
        }

        logs();

		// log("First Joint AntiGrav: "+getAntiGravFirstJoint());
		// log("Second Joint AntiGrav: "+getAntiGravSecondJoint());
        switch(theStateOf1) {
        case OFF:
            break;
        case ANTIGRAV:
            if (stowed){
                firstJoint.set(0);
            } else {
                antiGravFirstJoint();
            }
            break;
        case PID:
            firstJointPIDController.setReference(
				ArmsUtil.degreesToEU(firstJointPosition),
				ControlType.kSmartMotion,
				0,
				antiGrav.getFirstJointPower());

			if (Math.abs(ArmsUtil.EUTodegrees(firstJoint.getSensorPosition()) - firstJointPosition) <= doubleDeadZone){
				firstJointCombo ++;
			} else {
				firstJointCombo = 0;
			}

            // TODO: we can actually remove this 'combo' logic since we have found that the lack of EUTodegrees made the deadzone calculation wonky

            if (firstJointCombo >= 15){
				firstJointCombo = 0;
                // TODO: we do not want to do this here as arm may still be moving due to inertia
                //resetFirstEncoders();
                theStateOf1 = ArmState.ANTIGRAV;
            }

            break;
        }
        
        switch(theStateOf2) {
        case OFF:
            break;
        case ANTIGRAV:
            if (stowed){
                secondJoint.set(0); //This will activate brake mode
            } else {
                antiGravSecondJoint();
            }
            break;
        case PID:
            secondJointPIDController.setReference(
				ArmsUtil.degreesToEU(secondJointPosition),
				ControlType.kSmartMotion,
				0,
				antiGrav.getSecondJointPower());
            
			if (Math.abs(ArmsUtil.EUTodegrees(secondJoint.getSensorPosition()) - secondJointPosition) <= doubleDeadZone){
				secondJointCombo ++;
			} else {
				secondJointCombo = 0;
			}

            // TODO: we can actually remove this 'combo' logic since we have found that the lack of EUTodegrees made the deadzone calculation wonky

			if (secondJointCombo >= 15){
                secondJointCombo = 0;
                // TODO: we do not want to do this here as arm may still be moving due to inertia
                //resetSecondEncoders();
				theStateOf2 = ArmState.ANTIGRAV;
			}

            break;
        }

        // log("First" + EUTodegrees(firstJoint.getSensorPosition()) );
        // log(" Second" + EUTodegrees(secondJoint.getSensorPosition()));
        // log(theStateOf2 + "");
        // log("Difference: " + EUTodegrees(firstJoint.getSensorPosition()));

        // update shuffleboard periodically
        // SmartDashboard.putNumber("Alt Encoder 1", altEncoder1.getPosition());
        // SmartDashboard.putNumber("Alt Encoder 2", altEncoder2.getPosition());
    }
}

/* ~~ Code Review ~~
    Use Voltage Control Mode when setting power (refer to CANSparkMaxMotorController.java)
    Maybe use Nicholas's formula for degrees to EU
    "Use break mode" - Ronald the not programmer
 */
