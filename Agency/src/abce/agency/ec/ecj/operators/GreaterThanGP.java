package abce.agency.ec.ecj.operators;


import abce.agency.ec.ecj.types.*;
import ec.*;
import ec.gp.*;



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
	}



	@Override
	public String toString() {
		return ">";
	}

}
