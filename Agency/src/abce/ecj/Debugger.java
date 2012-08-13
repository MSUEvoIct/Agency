package abce.ecj;


import ec.EvolutionState;
import ec.Setup;
import ec.util.Parameter;



/**
 * Debugger provides a means of enabling debugging code and configuring debug
 * methods
 * 
 * @author ruppmatt
 * 
 */
public class Debugger implements Setup {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	// Information about the values produced by nodes is logged
	public static final boolean	DEBUG_NODE_VALUES	= true;

	public static final String	P_BASE				= "debug";

	public static final String	P_NODE				= "node";
	public static final String	P_NODE_MODULO		= "modulo";
	public static final String	P_NODE_PROB			= "prob";

	public int					V_NODE_MODULO		= 0;
	public double				V_NODE_PROB			= 0.0;
	public String				V_NODE_FILEPATH		= "node_stats.dat.gz";



	public Parameter getDefaultBase() {
		return new Parameter(P_BASE);
	}



	@Override
	public void setup(EvolutionState state, Parameter base) {
		if (DEBUG_NODE_VALUES) {
			V_NODE_MODULO = state.parameters.getInt(getDefaultBase().push(P_NODE).push(P_NODE_MODULO), null);
			V_NODE_PROB = state.parameters.getDouble(getDefaultBase().push(P_NODE).push(P_NODE_PROB), null);
			//System.err.println("Modulo:" + V_NODE_MODULO + " prob:" + V_NODE_PROB);

		}
	}
}
