package abce.agency.ec.ecj.operators;


import abce.agency.ec.ecj.types.DoubleGP;
import abce.ecj.Debugger;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;



public class AddGP extends GPNode {

	private static final long	serialVersionUID	= 1L;



	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {

		DoubleGP result = (DoubleGP) input;

		this.children[0].eval(state, thread, result, stack, individual, problem);
		double first = result.value;

		this.children[1].eval(state, thread, result, stack, individual, problem);

		result.value += first;

		// This is debug code and should not be enabled in most production-style
		// experiments
		if (Debugger.DEBUG_NODE_VALUES)
			GPNodeDebug.debug(state, thread, result, this, "real");

	}



	@Override
	public String toString() {
		return "+";
	}

}
