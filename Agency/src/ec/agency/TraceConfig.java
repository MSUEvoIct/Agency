package ec.agency;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class TraceConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	private static TraceConfig singleton;
	
	public static TraceConfig getTraceConfig() {
		if (singleton == null)
			singleton = new TraceConfig();
		return singleton;
	}

	public final boolean traceEnabled;

	private Map<Class<? extends AgencyAction>, Boolean> actionTraces;
	
	public TraceConfig() {
		traceEnabled = true;
		actionTraces = new HashMap<Class<? extends AgencyAction>,Boolean>();
	}
	
	
	/**
	 * @param actionClass
	 * @return true if the specified action should emit trace information, false otherwise.
	 */
	public boolean isTraced(Class<? extends AgencyAction> actionClass) {
		if (!traceEnabled)
			return false;
		
		Boolean trace = actionTraces.get(actionClass);
		if (trace == null)
			return false;
		else
			return trace;
	}
	
	
}
