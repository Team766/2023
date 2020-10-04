package com.team766.framework;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

public abstract class Mechanism extends Loggable implements Runnable {
	private Context m_owningContext = null;
	
	public Mechanism() {
		Scheduler.getInstance().add(this);
	}

	public String getName() {
		return this.getClass().getName();
	}
	
	protected void checkContextOwnership() {
		if (Context.currentContext() != m_owningContext) {
			String message = getName() + " tried to be used by " + Context.currentContext().getContextName();
			if (m_owningContext != null) {
				message += " while owned by " + m_owningContext.getContextName();
			} else {
				message += " without taking ownership of it";
			}
			Logger.get(Category.PROCEDURES).logRaw(Severity.ERROR, message);
			throw new IllegalStateException(message);
		}
	}
	
	void takeOwnership(Context context, Context parentContext) {
		if (m_owningContext != null && m_owningContext == parentContext) {
			Logger.get(Category.PROCEDURES).logRaw(Severity.INFO, context.getContextName() + " is inheriting ownership of " + getName() + " from " + parentContext.getContextName());
		} else {
			Logger.get(Category.PROCEDURES).logRaw(Severity.INFO, context.getContextName() + " is taking ownership of " + getName());
			if (m_owningContext != null && m_owningContext != context) {
				Logger.get(Category.PROCEDURES).logRaw(Severity.WARNING, "Stopping previous owner of " + getName() + ": " + m_owningContext.getContextName());
				m_owningContext.stop();
			}
		}
		m_owningContext = context;
	}

	void releaseOwnership(Context context) {
		if (m_owningContext != context) {
			Logger.get(Category.PROCEDURES).logRaw(Severity.ERROR, context.getContextName() + " tried to release ownership of " + getName() + " but it doesn't own it");
			return;
		}
		Logger.get(Category.PROCEDURES).logRaw(Severity.INFO, context.getContextName() + " is releasing ownership of " + getName());
		m_owningContext = null;
	}

	@Override
	public void run () {}
}
