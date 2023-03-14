package com.team766.robot.mechanisms;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;
import com.revrobotics.CANSparkMax;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.library.RateLimiter;

//This is for the motor that controls the pulley
public class Arms extends Mechanism {
    
    private MotorController firstJoint = RobotProvider.instance.getMotor("arms.firstJoint");
    private CANSparkMax firstJointEx = (CANSparkMax)firstJoint;
    private SparkMaxPIDController firstJointPid  = firstJointEx.getPIDController();
    private SparkMaxAbsoluteEncoder firstJointAbsEncoder = firstJointEx.getAbsoluteEncoder(Type.kDutyCycle);
    private RelativeEncoder firstJointMainEncoder = firstJointEx.getEncoder();
    private double firstJointTargetPosition = -1;
    private boolean firstJointAutomaticReached = true;

    private static final double firstJointAntigravConstant = .021;

    private MotorController secondJoint = RobotProvider.instance.getMotor("arms.secondJoint");
    private CANSparkMax secondJointEx = (CANSparkMax)secondJoint;
    private SparkMaxPIDController secondJointPid = secondJointEx.getPIDController();
    private SparkMaxAbsoluteEncoder secondJointAbsEncoder = secondJointEx.getAbsoluteEncoder(Type.kDutyCycle);
    private RelativeEncoder secondJointMainEncoder = secondJointEx.getEncoder();
    private double secondJointTargetPosition = -1;
    private boolean secondJointAutomaticReached = true;

    private static final double secondJointAntigravConstant = .011;

    private static final double jointDeadZone = 0.004d;

    private boolean automaticMode = false;

    // we only want to update at most once per 20ms
    private RateLimiter runRateLimiter = new RateLimiter(0.02);

    /* 
    private MotorController thirdJoint = RobotProvider.instance.getMotor("arms.thirdJoint");
    private CANSparkMax thirdJointCSM = (CANSparkMax)thirdJoint;
    private SparkMaxPIDController thirdJointPID = thirdJointCSM.getPIDController();
    */

    public Arms() {
        /*
        Please dont actually use these pid values rn bc they havent been tested!!!!
        */

        firstJointPid.setFeedbackDevice(firstJointAbsEncoder);
        firstJointEx.setInverted(false);
        firstJointPid.setP(0);
        firstJointPid.setI(0);
        firstJointPid.setD(0.0005000000237487257);
        firstJointPid.setFF(0.0018999995663762093);
        firstJointPid.setSmartMotionMaxVelocity(6000, 0);
        firstJointPid.setSmartMotionMinOutputVelocity(0, 0);
        firstJointPid.setSmartMotionMaxAccel(3000, 0);
        firstJointPid.setOutputRange(-0.75, 0.75);

        secondJointPid.setFeedbackDevice(secondJointAbsEncoder);
        secondJointEx.setInverted(false);
        secondJointPid.setP(0.00008599997090641409);
        secondJointPid.setI(0);
        secondJointPid.setD(0);
        secondJointPid.setFF(0.0018699999307282269);
        secondJointPid.setSmartMotionMaxVelocity(2500, 0);
        secondJointPid.setSmartMotionMinOutputVelocity(0, 0);
        secondJointPid.setSmartMotionMaxAccel(1500, 0);
        secondJointPid.setOutputRange(-1, 1);
        
        firstJointTargetPosition = firstJointAbsEncoder.getPosition();
        secondJointTargetPosition = secondJointAbsEncoder.getPosition();
    }

    /**
     * Manually set power of 1st joint motor
     * - We should never really use this, for debugging purposes only
     * @param power
     */
    public void setFirstJointPower(double power) {
        checkContextOwnership();
        automaticMode = false;
        firstJoint.set(power);
    }
    
    /**
     * Manually set power of 2nd joint motor
     * - We should never really use this, for debugging purposes only
     * @param power
     */
    public void setSecondJointPower(double power) {
        checkContextOwnership();
        automaticMode = false;
        secondJoint.set(power);
    }

    // Getter method for getting the first arms encoder distance
    public double getEncoderDistanceOfFirstJoint() {
        return firstJoint.getSensorPosition();
    }

    public double getEncoderDistanceOfSecondJoint() {
        return secondJoint.getSensorPosition();
    }

    // resetting the encoder distance to zero for use without absolutes
    public void resetEncoders() {
        checkContextOwnership();
        firstJoint.setSensorPosition(0);
        secondJoint.setSensorPosition(0);
    }

    /**
     * Set joint target positions
     * - This enables automatic joint movement and holding
     * - Does not need to be called repeatedly
     * @param firstJointPosition in encoder units
     * @param secondJointPosition in encoder units
     */
    public void setAutomaticJointTarget(double firstJointPosition, double secondJointPosition) {
        setAutomaticFirstJointTarget(firstJointPosition);
        setAutomaticSecondJointTarget(secondJointPosition);
    }

    public void setAutomaticFirstJointTarget(double position) {
        checkContextOwnership();
        automaticMode = true;
        firstJointTargetPosition = position;
        firstJointAutomaticReached = false;

        // kick off smart motion to move towards target position based on absolute encoder
        firstJointPid.setFeedbackDevice(firstJointAbsEncoder);
        firstJointPid.setReference(position, CANSparkMax.ControlType.kSmartMotion);
    }

    public void setAutomaticSecondJointTarget(double position) {
        checkContextOwnership();
        automaticMode = true;
        secondJointTargetPosition = position;
        secondJointAutomaticReached = false;

        // kick off smart motion to move towards target position based on absolute encoder
        secondJointPid.setFeedbackDevice(secondJointAbsEncoder);
        secondJointPid.setReference(position, CANSparkMax.ControlType.kSmartMotion);
    }
	
	// antigrav
    public void updateArmsAntigrav() {
        checkContextOwnership();
        updateFirstJointAntigrav();
        updateSecondJointAntigrav();
    }

    private void updateFirstJointAntigrav() {
        // TODO: Question: does this not depend on an assumed initial encoder zero position ?
        firstJoint.set((-Math.sin((Math.PI / 88) * firstJointMainEncoder.getPosition())) * firstJointAntigravConstant);
    }

    private void updateSecondJointAntigrav() {
        // TODO: Question: does this not depend on an assumed initial encoder zero position ?
        secondJoint.set((-Math.sin((Math.PI / 88)* secondJointMainEncoder.getPosition())) * secondJointAntigravConstant);
    }

    /**
     * Periodic update, call every 20ms
     */
    @Override
    public void run () {
        // limits to once every X ms
        if(!runRateLimiter.next()) return;

        checkContextOwnership();

        // automatically move and stop
        if(automaticMode) {
            if(firstJointAutomaticReached || isWithinDeadzone(firstJointAbsEncoder.getPosition(), firstJointTargetPosition, jointDeadZone)) {

                firstJointAutomaticReached = true;
                updateFirstJointAntigrav();
            }

            if(secondJointAutomaticReached || isWithinDeadzone(secondJointAbsEncoder.getPosition(), secondJointTargetPosition, jointDeadZone)) {

                secondJointAutomaticReached = true;
                updateSecondJointAntigrav();
            }
        }
    }

	// changing degrees to encoder units for the non absolute encoder
    public static double degreesToEU(double angle) {
        return angle * (44.0 / 90);
    }

    /**
     * Checks if given value is within the deadzone range of dzCenter-dzSpread to dzCenter+dzSpread
     * @param value
     * @param dzCenter
     * @param dzSpread
     * @return
     */
    public static boolean isWithinDeadzone(double value, double dzCenter, double dzSpread) {
        return value > dzCenter - dzSpread && value < dzCenter + dzSpread;
    }
	    
}

/* ~~ Code Review ~~
    Use Voltage Control Mode when setting power (refer to CANSparkMaxMotorController.java)

    Maybe use Nicholas's formula for degrees to EU
    "Use break mode" - Ronald te not programmer

 */
