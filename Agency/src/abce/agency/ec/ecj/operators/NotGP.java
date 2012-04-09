package abce.agency.ec.ecj.operators;


import abce.agency.ec.ecj.types.*;
import ec.*;
import ec.gp.*;



public class NotGP extends GPNode {

	private static final long	serialVersionUID	= 1L;



	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		BooleanGP result = (BooleanGP) input;

		this.children[0].eval(state, thread, result, stack, individual, problem);
		result.value = (result.value) ? false : true;
	}



	@Override
	public String toString() {
		return "NOT";
	}

}
