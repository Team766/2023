package com.team766.frc2020.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.SolenoidController;
import com.team766.hal.SpeedController;

public class Intake extends Mechanism {
    private SolenoidController m_intakeArm;
    private SpeedController m_intakeWheels;

    public Intake() {
        m_intakeArm = RobotProvider.instance.getSolenoid("intakeArm");
        m_intakeWheels = RobotProvider.instance.getMotor("intake");
    }
    
    public void setIntakePower(double intakePower) {
		m_intakeWheels.set(intakePower);
    }
    
    public void setIntakeArm(boolean state) {
        m_intakeArm.set(state);
    }
}