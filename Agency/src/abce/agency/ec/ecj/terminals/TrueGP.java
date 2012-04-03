package abce.agency.ec.ecj.terminals;

import abce.agency.ec.ecj.types.*;
import ec.*;
import ec.gp.*;


public class TrueGP extends GPNode {

	private static final long	serialVersionUID	= 1L;



	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		BooleanGP b = (BooleanGP) input;
		b.value = true;
	}



	@Override
	public String toString() {
		return "true";
	}

}
