package com.team766.framework;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

public abstract class Mechanism implements Runnable {
	private Command m_runningCommand = null;
	
	public Mechanism() {
		Scheduler.getInstance().add(this);
	}

	public String getMechanismName() {
		return this.getClass().getName();
	}
	
	void takeControl(Command command) {
		Logger.get(Category.COMMANDS).logRaw(Severity.INFO, command.getCommandName() + " is taking control of " + getMechanismName());
		if (m_runningCommand != null) {
			Logger.get(Category.COMMANDS).logRaw(Severity.WARNING, "Stopping previous owner of " + getMechanismName() + ": " + m_runningCommand.getCommandName());
			m_runningCommand.stop();
		}
		m_runningCommand = command;
	}

	@Override
	public void run () {}
}
