package abce.agency.ec.ecj.operators;


import abce.agency.ec.ecj.Debugger;
import abce.agency.ec.ecj.types.BooleanGP;
import abce.agency.ec.ecj.types.DoubleGP;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;



public class GreaterThanGP extends GPNode {

	private static final long	serialVersionUID	= 1L;



	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		BooleanGP result = (BooleanGP) input;

		DoubleGP first = new DoubleGP();
		DoubleGP second = new DoubleGP();

		this.children[0].eval(state, thread, first, stack, individual, problem);
		this.children[1].eval(state, thread, second, stack, individual, problem);

		if (first.value > second.value)
			result.value = true;
		else
			result.value = false;

		// This is debug code and should not be enabled in most production-style
		// experiments
		if (Debugger.DEBUG_NODE_VALUES)
			GPNodeDebug.debug(state, thread, input, this, "bool");

	}



	@Override
	public String toString() {
		return ">";
	}

}
