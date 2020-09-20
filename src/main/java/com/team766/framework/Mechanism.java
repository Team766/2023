package com.team766.framework;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

public abstract class Mechanism extends Loggable implements Runnable {
	private Context m_controllingContext = null;
	
	public Mechanism() {
		Scheduler.getInstance().add(this);
	}

	public String getName() {
		return this.getClass().getName();
    }
    
    protected void checkContextControl() {
        if (Context.currentContext() != m_controllingContext) {
            String message = getName() + " tried to be used by " + Context.currentContext().getContextName() + " while controlled by " + m_controllingContext.getContextName();
            Logger.get(Category.PROCEDURES).logRaw(Severity.ERROR, message);
            throw new IllegalStateException(message);
        }
    }
    
    void takeControl(Context context, Context parentContext) {
        if (m_controllingContext != null && m_controllingContext == parentContext) {
            Logger.get(Category.PROCEDURES).logRaw(Severity.INFO, context.getContextName() + " is ineriting control of " + getName() + " from " + parentContext.getContextName());
        } else {
            Logger.get(Category.PROCEDURES).logRaw(Severity.INFO, context.getContextName() + " is taking control of " + getName());
            if (m_controllingContext != null && m_controllingContext != context) {
                Logger.get(Category.PROCEDURES).logRaw(Severity.WARNING, "Stopping previous owner of " + getName() + ": " + m_controllingContext.getContextName());
                m_controllingContext.stop();
            }
        }
		m_controllingContext = context;
    }

	@Override
	public void run () {}
}
