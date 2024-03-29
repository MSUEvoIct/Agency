package ec.agency.gp.operators;


import ec.EvolutionState;
import ec.Problem;
import ec.agency.Debugger;
import ec.agency.gp.types.BooleanGP;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;



/**
 * XOrGP returns true if one and only one of its children return true. If either
 * both children return true or both children return false, XOrGP returns false.
 * 
 * @author kkoning
 * 
 */
public class XOrGP extends GPNode {

	private static final long	serialVersionUID	= 1L;



	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		BooleanGP result = (BooleanGP) input;

		this.children[0].eval(state, thread, result, stack, individual, problem);
		boolean first = result.value;

		this.children[1].eval(state, thread, result, stack, individual, problem);
		boolean second = result.value;

		if (first == second) // either FALSE/FALSE, or TRUE/TRUE
			result.value = false; // We return FALSE
		else
			// otherwise TRUE/FALSE or FALSE/TRUE
			result.value = true; // we return TRUE

		// This is debug code and should not be enabled in most production-style
		// experiments
		if (Debugger.DEBUG_NODE_VALUES)
			GPNodeDebug.debug(state, thread, input, this, "bool");

	}



	@Override
	public String toString() {
		return "xor";
	}

}
