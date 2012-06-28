package abce.agency;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import abce.agency.actions.SimulationAction;

public class TraceConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	private static TraceConfig singleton;
	
	public static TraceConfig getTraceConfig() {
		if (singleton == null)
			singleton = new TraceConfig();
		return singleton;
	}

	public final boolean traceEnabled;

	private Map<Class<? extends SimulationAction>, Boolean> actionTraces;
	
	public TraceConfig() {
		traceEnabled = true;
		actionTraces = new HashMap<Class<? extends SimulationAction>,Boolean>();
	}
	
	
	/**
	 * @param actionClass
	 * @return true if the specified action should emit trace information, false otherwise.
	 */
	public boolean isTraced(Class<? extends SimulationAction> actionClass) {
		if (!traceEnabled)
			return false;
		
		Boolean trace = actionTraces.get(actionClass);
		if (trace == null)
			return false;
		else
			return trace;
	}
	
	
}
