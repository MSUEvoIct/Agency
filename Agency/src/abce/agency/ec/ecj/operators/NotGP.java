package abce.agency.ec.ecj.operators;


import abce.agency.ec.ecj.types.*;
import ec.*;
import ec.gp.*;



/**
 * Not returns true only if both of its children return false. If one of its
 * children returns true, it returns false.
 * 
 * @author kkoning
 * 
 */
public class NotGP extends GPNode {

	private static final long	serialVersionUID	= 1L;



	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		BooleanGP result = (BooleanGP) input;

		this.children[0].eval(state, thread, result, stack, individual, problem);
		if (result.value) { // if one was true,
			result.value = false; // the NOT is false
			return;
		}
		this.children[1].eval(state, thread, result, stack, individual, problem);
		if (result.value) { // if one was true,
			result.value = false; // the NOT is false
			return;
		}

		// We only get here if both children were false
		result.value = true;
	}



	@Override
	public String toString() {
		return "not";
	}

}
