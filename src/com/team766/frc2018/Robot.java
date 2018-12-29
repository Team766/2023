package com.team766.frc2018;

import com.team766.framework.Command;
import com.team766.frc2018.mechanisms.Arm;
import com.team766.frc2018.mechanisms.Drive;
import com.team766.frc2018.mechanisms.Wrist;
import com.team766.frc2018.sequences.ExampleDriveSequence;
import com.team766.frc2018.sequences.ExampleSequence;
import com.team766.hal.MyRobot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class Robot extends MyRobot {	
	public Arm arm;
	public Wrist wrist;
	public Drive drive;
	
	private OI m_oi;
	
	private SendableChooser<Command> m_chooser = new SendableChooser<Command>();
	private Command m_autonomous;
	
	@Override
	public void robotInit() {
		drive = new Drive();
		arm = new Arm(this);
		wrist = new Wrist(this);
		
		m_oi = new OI(this);
		
		m_chooser.addDefault("Autonomous1", new ExampleDriveSequence(this));
		m_chooser.addObject("Autonomous2", new ExampleSequence(this));
	}
	
	@Override
	public void autonomousInit() {
		m_autonomous = m_chooser.getSelected();
		m_autonomous.start();
	}
	
	@Override
	public void teleopInit() {
		if (m_autonomous != null) {
			m_autonomous.stop();
		}
	}
	
	@Override
	public void teleopPeriodic() {
		m_oi.run();
	}
}
