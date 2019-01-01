package com.team766.framework;

public abstract class Mechanism implements Runnable {
	private Command m_runningCommand = null;
	
	public Mechanism() {
		Scheduler.getInstance().add(this);
	}
	
	void takeControl(Command command) {
		if (m_runningCommand != null) {
			m_runningCommand.stop();
		}
		m_runningCommand = command;
	}
}
