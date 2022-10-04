package com.team766.framework;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;

public abstract class Mechanism extends LoggingBase {
	private Context m_owningContext = null;
	private Thread m_runningPeriodic = null;
	
	public Mechanism() {
		loggerCategory = Category.MECHANISMS;

		Scheduler.getInstance().add(new Runnable() {
			@Override
			public void run() {
				try {
					Mechanism.this.m_runningPeriodic = Thread.currentThread();
					Mechanism.this.run();
				}
				finally {
					Mechanism.this.m_runningPeriodic = null;
				}
			}

			@Override
			public String toString() {
				String repr = Mechanism.this.getName();
				if (Mechanism.this.m_runningPeriodic != null) {
					repr += " running\n" + StackTraceUtils.getStackTrace(m_runningPeriodic);
				}
				return repr;
			}
		});
	}

	public String getName() {
		return this.getClass().getName();
	}
	
	protected void checkContextOwnership() {
		if (Context.currentContext() != m_owningContext && m_runningPeriodic == null) {
			String message = getName() + " tried to be used by " + Context.currentContext().getContextName();
			if (m_owningContext != null) {
				message += " while owned by " + m_owningContext.getContextName();
			} else {
				message += " without taking ownership of it";
			}
			Logger.get(Category.FRAMEWORK).logRaw(Severity.ERROR, message);
			throw new IllegalStateException(message);
		}
	}
	
	void takeOwnership(Context context, Context parentContext) {
		if (m_owningContext != null && m_owningContext == parentContext) {
			Logger.get(Category.FRAMEWORK).logRaw(Severity.INFO, context.getContextName() + " is inheriting ownership of " + getName() + " from " + parentContext.getContextName());
		} else {
			Logger.get(Category.FRAMEWORK).logRaw(Severity.INFO, context.getContextName() + " is taking ownership of " + getName());
			while (m_owningContext != null && m_owningContext != context) {
				Logger.get(Category.FRAMEWORK).logRaw(Severity.WARNING, "Stopping previous owner of " + getName() + ": " + m_owningContext.getContextName());
				m_owningContext.stop();
				var stoppedContext = m_owningContext;
				context.yield();
				if (m_owningContext == stoppedContext) {
					Logger.get(Category.FRAMEWORK).logRaw(Severity.ERROR, "Previous owner of " + getName() + ", " + m_owningContext.getContextName() + " did not release ownership when requested. Release will be forced.");
					m_owningContext.releaseOwnership(this);
					break;
				}
			}
		}
		m_owningContext = context;
	}

	void releaseOwnership(Context context) {
		if (m_owningContext != context) {
			LoggerExceptionUtils.logException(new Exception(context.getContextName() + " tried to release ownership of " + getName() + " but it doesn't own it"));
			return;
		}
		Logger.get(Category.FRAMEWORK).logRaw(Severity.INFO, context.getContextName() + " is releasing ownership of " + getName());
		m_owningContext = null;
	}

	public void run () {}
}
