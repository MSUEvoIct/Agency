package abce.agency.ec.ecj.operators;


import abce.agency.ec.ecj.types.*;
import abce.ecj.*;
import ec.*;
import ec.gp.*;



public class MultiplyGP extends GPNode {

	private static final long	serialVersionUID	= 1L;



	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		DoubleGP result = (DoubleGP) input;

		this.children[0].eval(state, thread, result, stack, individual, problem);
		double first = result.value;

		this.children[1].eval(state, thread, result, stack, individual, problem);

		result.value *= first;

		// This is debug code and should not be enabled in most production-style
		// experiments
		if (Debugger.DEBUG_NODE_VALUES)
			GPNodeDebug.debug(state, thread, input, this, "real");

	}



	@Override
	public String toString() {
		return "*";
	}

}
