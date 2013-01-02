package abce.agency.ec.ecj.terminals;

import abce.agency.ec.ecj.Debugger;
import abce.agency.ec.ecj.operators.GPNodeDebug;
import abce.agency.ec.ecj.types.DoubleGP;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

/**
 * Provide a constant 100.0 value
 * 
 * @author ruppmatt
 *
 */
public class Const100DoubleGP extends GPNode{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack adf,
			GPIndividual ind, Problem problem) {
		DoubleGP d = (DoubleGP) input;
		d.value = 100.0;
		
		if (Debugger.DEBUG_NODE_VALUES)
			GPNodeDebug.debug(state, thread, input, this, "double");

		
	}

	@Override
	public String toString() {
		return "100.0";
	}

}
