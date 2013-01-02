package ec.agency.gp.terminals;


import ec.EvolutionState;
import ec.Problem;
import ec.agency.Debugger;
import ec.agency.gp.operators.GPNodeDebug;
import ec.agency.gp.types.BooleanGP;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;



public class FalseGP extends GPNode {

	private static final long	serialVersionUID	= 1L;



	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		BooleanGP b = (BooleanGP) input;
		b.value = false;

		// This is debug code and should not be enabled in most production-style
		// experiments
		if (Debugger.DEBUG_NODE_VALUES)
			GPNodeDebug.debug(state, thread, input, this, "bool");

	}



	@Override
	public String toString() {
		return "false";
	}

}
