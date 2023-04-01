package com.team766.robot.mechanisms;

import com.team766.config.ConfigFileReader;
import com.team766.hal.MotorController;
import com.team766.library.ValueProvider;

public class ArmsAntiGrav {

	private ValueProvider<Double> ANTI_GRAV_FIRST_JOINT = ConfigFileReader.getInstance().getDouble("arms.antiGravFirstJoint");
    private ValueProvider<Double> ANTI_GRAV_SECOND_JOINT = ConfigFileReader.getInstance().getDouble("arms.antiGravSecondJoint");
    private static final double ANTI_GRAV_FIRSTSECOND_JOINT = 0.001;

	private MotorController firstJoint;
	private MotorController secondJoint;

	public ArmsAntiGrav(MotorController j1, MotorController j2) {
		firstJoint = j1;
		secondJoint = j2;
	}

    public void updateFirstJoint() {
        firstJoint.set(getFirstJointPower());
    }

    public void updateSecondJoint() {
        secondJoint.set(getSecondJointPower());
    }

    // Unused
    /*private double betterGetAntiGravFirstJoint(){
        double firstRelEncoderAngle = EUTodegrees(firstJoint.getSensorPosition());
        double secondRelEncoderAngle = EUTodegrees(secondJoint.getSensorPosition());
        double massRatio = 2; //ratio between firstJoint and secondJoint
        double triangleSide1 = 38; // firstJoint length
        double triangleSide2 = 38; // half secondJoint length
        double middleAngle = 180-(secondRelEncoderAngle-firstRelEncoderAngle);
        double triangleSide3 = lawOfCosines(triangleSide1,triangleSide2,middleAngle);
        double firstSecondJointAngle = firstRelEncoderAngle+lawOfSines(triangleSide3,middleAngle,triangleSide2);
        double firstJointAngle = 90-Math.abs(firstRelEncoderAngle);
        return (-1*Math.signum(firstRelEncoderAngle) * Math.cos((Math.PI / 180) * firstJointAngle) * ANTI_GRAV_FIRST_JOINT.valueOr(0.0)) + (-1*Math.signum(firstSecondJointAngle)*triangleSide3 * Math.sin((Math.PI / 180)*firstSecondJointAngle) * ANTI_GRAV_FIRSTSECOND_JOINT);
    }*/

    public double getFirstJointPower() {
        double firstRelEncoderAngle = ArmsUtil.EUTodegrees(firstJoint.getSensorPosition());
        double firstJointAngle = 90-Math.abs(firstRelEncoderAngle);
        return -1*Math.signum(firstRelEncoderAngle) * (Math.cos((Math.PI / 180) * firstJointAngle) * ANTI_GRAV_FIRST_JOINT.valueOr(0.0));
    }

    public double getSecondJointPower() {
        double secondRelEncoderAngle = ArmsUtil.EUTodegrees(secondJoint.getSensorPosition());
        double secondJointAngle = 90-Math.abs(secondRelEncoderAngle);
        return -1*Math.signum(secondRelEncoderAngle) * (Math.cos((Math.PI / 180) * secondJointAngle) * ANTI_GRAV_SECOND_JOINT.valueOr(0.0));
    }

}
