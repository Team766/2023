package com.team766.framework;

import edu.wpi.first.wpilibj2.command.Command;

/**
 * This wraps a class that confroms to WPILib's Command interface, and allows
 * it to be used in the Maroon Framework as a Procedure.
 */
public class WPILibCommandProcedure extends Procedure {

    private final Command command;
    private Mechanism[] requirements;

    /**
     * @param command The WPILib Command to adapt
     * @param requirements This Procedure will take ownership of the Mechanisms
     *                     given here during the time it is executing.
     */
    public WPILibCommandProcedure(Command command, Mechanism... requirements) {
        this.command = command;
        this.requirements = requirements;
    }

    @Override
    public void run(Context context) {
        for (Mechanism req : this.requirements) {
            context.takeOwnership(req);
        }
        boolean interrupted = false;
        try {
            this.command.initialize();
            while (!this.command.isFinished()) {
                this.command.execute();
                context.yield();
            }
        } catch (Throwable ex) {
            interrupted = true;
            this.command.cancel();
            throw ex;
        } finally {
            this.command.end(interrupted);
            for (Mechanism req : this.requirements) {
                context.releaseOwnership(req);
            }
        }
    }

}