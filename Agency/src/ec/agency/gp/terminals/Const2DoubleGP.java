package ec.agency.gp.terminals;

import ec.EvolutionState;
import ec.Problem;
import ec.agency.Debugger;
import ec.agency.gp.operators.GPNodeDebug;
import ec.agency.gp.types.DoubleGP;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

/**
 * Provide a constant 2.0 value
 * 
 * @author ruppmatt
 *
 */
public class Const2DoubleGP extends GPNode{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack adf,
			GPIndividual ind, Problem problem) {
		DoubleGP d = (DoubleGP) input;
		d.value = 2.0;
		
		if (Debugger.DEBUG_NODE_VALUES)
			GPNodeDebug.debug(state, thread, input, this, "double");

		
	}

	@Override
	public String toString() {
		return "2.0";
	}

}
