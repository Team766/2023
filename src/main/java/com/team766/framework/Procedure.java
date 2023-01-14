package com.team766.framework;

public abstract class Procedure extends LoggingBase implements RunnableWithContext {
	// A reusable Procedure that does nothing.
	private static final class NoOpProcedure extends Procedure {
		@Override
		public void run(Context context) {
		}
	}
	public static final Procedure NO_OP = new NoOpProcedure();

	private static int c_idCounter = 0;

	private static synchronized int createNewId() {
		return c_idCounter++;
	}
	
	protected final int m_id;

	public Procedure() {
		m_id = createNewId();
	}

	public String getName() {
		return this.getClass().getName() + "/" + m_id;
	}

	@Override
	public String toString() {
		return getName();
	}
}
