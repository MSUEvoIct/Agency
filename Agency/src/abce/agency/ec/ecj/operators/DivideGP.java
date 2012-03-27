package abce.agency.ec.ecj.operators;


import abce.agency.ec.ecj.types.*;
import ec.*;
import ec.gp.*;



/**
 * DivideGP returns the value of its first child divided by the value of its
 * second child. If its second child returns zero, it returns positive or
 * negative infinity, depending on whether the first child was positive or
 * negative, respectively. If both children are zero, it returns 1.0.
 * 
 * @author kkoning
 * 
 */
public class DivideGP extends GPNode {

	private static final long	serialVersionUID	= 1L;



	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		DoubleGP result = (DoubleGP) input;

		this.children[1].eval(state, thread, result, stack, individual, problem);
		double first = result.value;

		this.children[0].eval(state, thread, result, stack, individual, problem);

		if (first == 0.0) {
			if (result.value > 0.0)
				result.value = Double.POSITIVE_INFINITY;
			if (result.value < 0.0)
				result.value = Double.NEGATIVE_INFINITY;
			result.value = 1.0;
			return;
		}
		result.value /= first;
	}



	@Override
	public String toString() {
		return "/";
	}

}
