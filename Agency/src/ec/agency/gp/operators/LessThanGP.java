package ec.agency.gp.operators;


import ec.EvolutionState;
import ec.Problem;
import ec.agency.gp.types.BooleanGP;
import ec.agency.gp.types.DoubleGP;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;



public class LessThanGP extends GPNode {

	private static final long	serialVersionUID	= 1L;



	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		BooleanGP result = (BooleanGP) input;

		DoubleGP first = new DoubleGP();
		DoubleGP second = new DoubleGP();

		this.children[0].eval(state, thread, first, stack, individual, problem);
		this.children[1].eval(state, thread, second, stack, individual, problem);

		if (first.value < second.value)
			result.value = true;
		else
			result.value = false;

		// This is debug code and should not be enabled in most production-style
		// experiments
		GPNodeDebug.debug(state, thread, input, this, "bool");

	}



	@Override
	public String toString() {
		return "<";
	}

}
