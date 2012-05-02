package abce.agency.actions;

import java.io.*;
import abce.agency.engine.TraceConfig;

/**
 * All actions peformed by agents should be implemented using an extension of
 * this class. It enforces an explicit check for executability through
 * isAllowed(), and a description for tracing with describe(), rather than using
 * the exception system. This creates a better organization for verification of
 * any given action than having that code scattered about functions for
 * different classes which implement the action.
 * 
 * @author kkoning
 * 
 */
public abstract class SimulationAction implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Process the current action. The action first checks if it is allowed. If
	 * so, the execute() method is called, otherwise reject(). This will also
	 * output a trace (using describe()) if enabled in TraceConfig.
	 * 
	 * @return true if the action was allowed and executed, false otherwise.
	 */
	public final boolean process() {
		TraceConfig tc = TraceConfig.getTraceConfig();
		if (tc.traceEnabled) {
			if (tc.isTraced(this.getClass()))
				// TODO: Replace with some other output
				System.err.println(this.describe());
		}

		boolean allowed = isAllowed();
		if (allowed) {
			actualize();
			return true;
		} else {
			reject();
			return false;
		}
	}

	/**
	 * @return A human-readable description of the action, including any
	 *         parameters.
	 */
	protected abstract String describe();

	/**
	 * 
	 * 
	 * Checks to see if the action is allowed. The default implementation
	 * automatically rejects the action; action object <i>compile</i> without
	 * overriding verify() and/or reject(), but would always throw a
	 * RuntimeException.
	 * 
	 * @return True if the action is allowed and may be processed, false
	 *         otherwise.
	 */
	protected abstract boolean isAllowed();

	/**
	 * Handle the execution/actualiztion of this SimulationAction. This will
	 * usually involve distributing the action to various other objects (usually
	 * the parties to the transaction) to update themselves to reflect the fact
	 * that the transaction has taken place. For example, an action for the sale
	 * of goods would be sent to the selling firm, which would deplete its
	 * inventory and increase its cash by the purchase price. It would also be
	 * sent to the consumer for tracking purposes.
	 */
	protected abstract void actualize();

	/**
	 * Called if the action fails verification and is rejected. Derived actions
	 * should override this method if they wish to prevent failure from throwing
	 * a RuntimeExcetion will be thrown
	 */
	protected void reject() {
		throw new RuntimeException("Action " + this
				+ " rejected, but no reject function specified");
	}

}
