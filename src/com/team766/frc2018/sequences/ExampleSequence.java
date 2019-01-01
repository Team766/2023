package com.team766.frc2018.sequences;

import com.team766.framework.Subroutine;
import com.team766.frc2018.Robot;
import com.team766.frc2018.mechanisms.Arm;

public class ExampleSequence extends Subroutine {
	private Arm m_arm;
	
	public ExampleSequence(Robot robot) {
		m_arm = robot.arm;
		takeControl(m_arm);
	}
	
	protected void subroutine() {
		m_arm.setTargetUp();
		waitFor(() -> m_arm.atTarget());
		m_arm.setTargetDown();
		waitFor(() -> m_arm.atTarget());
	}
}