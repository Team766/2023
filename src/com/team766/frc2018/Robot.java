package com.team766.frc2018;

import com.team766.framework.Command;
import com.team766.frc2018.mechanisms.ExampleMechanism;
import com.team766.frc2018.sequences.ExampleSequence;
import com.team766.hal.JoystickReader;
import com.team766.hal.MyRobot;
import com.team766.hal.RobotProvider;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class Robot extends MyRobot {	
	public ExampleMechanism exampleMechanism;
	
	private SendableChooser<Command> m_chooser = new SendableChooser<Command>();
	private JoystickReader m_joystick;
	private Command m_autonomous;
	
	@Override
	public void robotInit() {
		exampleMechanism = new ExampleMechanism();
		m_joystick = RobotProvider.instance.getJoystick(1); 
		
		m_chooser.addObject("Autonomous1", new ExampleSequence(this));
		m_chooser.addObject("Autonomous2", new ExampleSequence(this));
		m_chooser.addObject("Autonomous3", new ExampleSequence(this));
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
		if (m_joystick.getTriggerPressed()) {
			new ExampleSequence(this).start();
		}
	}
}
