package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController;
import com.team766.hal.SolenoidController;

public class Intake extends Mechanism {
	private SolenoidController m_intakeArm1;
	private SolenoidController m_intakeArm2;
	private MotorController m_frontIntakeWheel;
	private MotorController m_middleIntakeWheel;

	//for now this is just a copy of the 2020 intake so config names are most likely wrong
	public Intake() {
		m_frontIntakeWheel = RobotProvider.instance.getMotor("intake.frontWheel");
		m_middleIntakeWheel = RobotProvider.instance.getMotor("intake.topWheel");
		m_intakeArm1 = RobotProvider.instance.getSolenoid("Intake.intakeArm1");
		m_intakeArm2 = RobotProvider.instance.getSolenoid("Intake.intakeArm2");
		m_frontIntakeWheel.setInverted(true);
	}

	public void IntakeIn() {
		checkContextOwnership();

		m_intakeArm1.set(true);
		m_intakeArm2.set(false);
		m_frontIntakeWheel.set(1.0);
		m_middleIntakeWheel.set(1.0);
	}

	public void IntakeOut(){
		checkContextOwnership();

		m_intakeArm1.set(true);
		m_intakeArm2.set(false);
		m_frontIntakeWheel.set(-1.0);
		m_middleIntakeWheel.set(-1.0);
	}

	public void stopIntake() {
		checkContextOwnership();

		m_middleIntakeWheel.set(0.0);
		m_frontIntakeWheel.set(0.0);
		m_intakeArm1.set(false);
		m_intakeArm2.set(true);
	}
}
