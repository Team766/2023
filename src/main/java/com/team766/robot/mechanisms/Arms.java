package com.team766.robot.mechanisms;

import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;
import com.revrobotics.CANSparkMax;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.library.RateLimiter;
import com.team766.library.ValueProvider;
import com.team766.logging.Category;
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
    private static final double doubleDeadZone = 2;

    enum ArmState{
        PID, // Moving
        ANTIGRAV // Holding
    };

    private ArmState firstJointState = ArmState.ANTIGRAV;
    private ArmState secondJointState = ArmState.ANTIGRAV;

    // We want firstJoint/secondJoint being straight-up to be 0 rel encoder units 
    // and counter-clockwise to be positive.
    // All the following variables are in degrees

    private double firstJointPosition = 0; 
    private double secondJointPosition = 0; 
	private double firstJointCombo = 0;
	private double secondJointCombo = 0;

    private static final double FIRST_JOINT_MAX_LOCATION = 35;
    private static final double FIRST_JOINT_MIN_LOCATION = -40;
    private static final double SECOND_JOINT_MAX_LOCATION = 45;
    private static final double SECOND_JOINT_MIN_LOCATION = -160;

	private RateLimiter runRateLimiter = new RateLimiter(0.05);

    private ArmsAntiGrav antiGrav;

    public Arms() {
		loggerCategory = Category.MECHANISMS;
		resetEncoders();

		ValueProvider<Double> firstJointP = ConfigFileReader.getInstance().getDouble("arms.firstJointP");
		ValueProvider<Double> firstJointI = ConfigFileReader.getInstance().getDouble("arms.firstJointI");
		ValueProvider<Double> firstJointD = ConfigFileReader.getInstance().getDouble("arms.firstJointD");
		ValueProvider<Double> firstJointFF = ConfigFileReader.getInstance().getDouble("arms.firstJointFF");

		firstJointPIDController.setP(firstJointP.valueOr(0.0006));
        firstJointPIDController.setI(firstJointI.valueOr(0.0));
        firstJointPIDController.setD(firstJointD.valueOr(0.0));
        firstJointPIDController.setFF(firstJointFF.valueOr(0.002));

		ValueProvider<Double> secondJointP = ConfigFileReader.getInstance().getDouble("arms.secondJointP");
		ValueProvider<Double> secondJointI = ConfigFileReader.getInstance().getDouble("arms.secondJointI");
		ValueProvider<Double> secondJointD = ConfigFileReader.getInstance().getDouble("arms.secondJointD");
		ValueProvider<Double> secondJointFF = ConfigFileReader.getInstance().getDouble("arms.secondJointFF");
		
        // TODO: use secondJointO/I/D/FF.valueOr
        secondJointPIDController.setP(0.0005);
        secondJointPIDController.setI(secondJointI.valueOr(0.0));
        secondJointPIDController.setD(0.00001);
        secondJointPIDController.setFF(0.00109);

        firstJointCANSparkMax.setInverted(false);
        firstJointPIDController.setSmartMotionMaxVelocity(4000, 0);
        firstJointPIDController.setSmartMotionMinOutputVelocity(0, 0);
        firstJointPIDController.setSmartMotionMaxAccel(3000, 0);

        firstJointPIDController.setOutputRange(-0.75, 0.75);
        firstJointCANSparkMax.setSmartCurrentLimit(40);

        secondJointPIDController.setSmartMotionMaxVelocity(4000, 0);
        secondJointPIDController.setSmartMotionMinOutputVelocity(0, 0);
        secondJointPIDController.setSmartMotionMaxAccel(3000, 0);
        secondJointPIDController.setOutputRange(-1, 1);
        secondJointCANSparkMax.setSmartCurrentLimit(40);

		firstJointPIDController.setFeedbackDevice(firstJointCANSparkMax.getEncoder());
		secondJointPIDController.setFeedbackDevice(secondJointCANSparkMax.getEncoder());
        
        // absolute encoder zero offsets are set to positions which will never be used
        // to avoid wrap-around errors
        // first joint zero is horizontal parallel to ground
        // second joint zero is when the relative angle is 0 degrees from the first arm segment
		altEncoder1.setZeroOffset(0.68);    // TODO: these need tweaking from altEncoder1Offset
		altEncoder2.setZeroOffset(0.62);

        antiGrav = new ArmsAntiGrav(firstJoint, secondJoint);
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

        // TODO: this offset is to factor in the difference between the
        //       "zero" for alt encoder and the "zero" when we use degrees
        //       Offset tuning should be done above in `altEncoder1.setZeroOffset`
        // double altEncoder1Offset = 0.25;
        final double altEncoder1Offset = 0.225;
        // double altEncoder2Offset = 0.5;
        final double altEncoder2Offset = 0.49;

        // altEncoder1Offset = what is the value of altEncoder1 when firstJoint is vertical
        double firstJointAbsEncoder = altEncoder1.getPosition();
        double firstJointRelEncoder = ArmsUtil.AbsToEU(firstJointAbsEncoder - altEncoder1Offset);

        // altEncoder2Offset = what is the value of altEncoder2 when secondJoint is colinear w/firstJoint
        double secondJointAbsEncoder = altEncoder2.getPosition();
        double secondJointRelEncoder = ArmsUtil.AbsToEU(
            firstJointAbsEncoder - altEncoder1Offset
            + secondJointAbsEncoder - altEncoder2Offset);

        // set the sensor positions of our rel encoders
        firstJoint.setSensorPosition(firstJointRelEncoder);
        secondJoint.setSensorPosition(secondJointRelEncoder);

        firstJointPosition = ArmsUtil.EUTodegrees(firstJointRelEncoder);
        secondJointPosition = ArmsUtil.EUTodegrees(secondJointRelEncoder);
    }

	// PID for first arm
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
        value = ArmsUtil.clampValueToRange(value, FIRST_JOINT_MAX_LOCATION, FIRST_JOINT_MIN_LOCATION);

        firstJointPosition = value;
        // if(Math.abs(EUTodegrees(firstJoint.getSensorPosition() )))
        firstJointPIDController.setReference(ArmsUtil.degreesToEU(firstJointPosition),
            ControlType.kSmartMotion,
            0,
            antiGrav.getFirstJointPower());
        firstJointState = ArmState.PID;
        firstJointCombo = 0;
    }

	// PID for second arm
    public void pidForArmTwo(double value){
        // log("Second Joint Absolute Encoder: " + altEncoder2.getPosition());
        // log("" + firstJointCANSparkMax.getAbsoluteEncoder(Type.kDutyCycle).getPosition());

        // If value is out of range, then adjust value.

        value = ArmsUtil.clampValueToRange(value, SECOND_JOINT_MAX_LOCATION, SECOND_JOINT_MIN_LOCATION);

        secondJointPosition = value;
        secondJointPIDController.setReference(
            ArmsUtil.degreesToEU(secondJointPosition),
            ControlType.kSmartMotion,
            0,
            antiGrav.getSecondJointPower());
        secondJointState = ArmState.PID;
        secondJointCombo = 0;
    }

    // Use these for manual pid based angle increment/decrement
    public void alterArmOneAngle(double degrees) {
        pidForArmOne(firstJointPosition + degrees);
    }

    public void alterArmTwoAngle(double degrees) {
        pidForArmTwo(firstJointPosition + degrees);
    }

    // These next 3 antiGrav aren't used.
    public void antiGravBothJoints() {
        antiGravFirstJoint();
        antiGravSecondJoint();
    }

    public void antiGravFirstJoint() {
        antiGrav.updateFirstJoint();
        firstJointState = ArmState.ANTIGRAV;
    }

    public void antiGravSecondJoint() {
        antiGrav.updateSecondJoint();
        secondJointState = ArmState.ANTIGRAV;
    }

	
    @Override
    public void run() {
		if(!runRateLimiter.next()) return;

        log("First Joint Absolute Encoder: " + altEncoder1.getPosition());
        log("Second Joint Absolute Encoder: " + altEncoder2.getPosition());
        // log("First Joint Relative Encoder: " + firstJoint.getSensorPosition());
        // log("Second Joint Relative Encoder: " + secondJoint.getSensorPosition());
        // log("First Joint Difference: " + (EUTodegrees(firstJoint.getSensorPosition())-firstJointPosition));
        // log("Second Joint Difference: " + (EUTodegrees(secondJoint.getSensorPosition())-secondJointPosition));
		log("Degrees Joint 1: "+ ArmsUtil.EUTodegrees(firstJoint.getSensorPosition()));
		log("Degrees Joint 2: "+ ArmsUtil.EUTodegrees(secondJoint.getSensorPosition()));
		log("First Joint State: "+firstJointState);
		log("Second Joint State: "+secondJointState);
        log("First Joint Combo: "+firstJointCombo);
        log("Second Joint Combo: "+secondJointCombo);

		// log("First Joint AntiGrav: "+getAntiGravFirstJoint());
		// log("Second Joint AntiGrav: "+getAntiGravSecondJoint());
        if (firstJointState == ArmState.ANTIGRAV) {
            antiGravFirstJoint();
        } else {
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

            if (firstJointCombo >= 10){
				firstJointCombo = 0;
                // TODO: we do not want to do this here as arm may still be moving due to inertia
                resetEncoders();
                firstJointState = ArmState.ANTIGRAV;
            }
        }
        
        if (secondJointState == ArmState.ANTIGRAV) {
            antiGravSecondJoint();
        } else {
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

			if (secondJointCombo >= 10){
                secondJointCombo = 0;
                // TODO: we do not want to do this here as arm may still be moving due to inertia
                resetEncoders();
				secondJointState = ArmState.ANTIGRAV;
			}
        }
    }
}

/* ~~ Code Review ~~
    Use Voltage Control Mode when setting power (refer to CANSparkMaxMotorController.java)
    Maybe use Nicholas's formula for degrees to EU
    "Use break mode" - Ronald the not programmer
 */