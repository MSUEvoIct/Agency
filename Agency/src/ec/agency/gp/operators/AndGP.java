package ec.agency.gp.operators;


import ec.EvolutionState;
import ec.Problem;
import ec.agency.Debugger;
import ec.agency.gp.types.BooleanGP;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;



/**
 * AndGP returns true if both of its children are true. Otherwise, it returns
 * false
 * 
 * @author kkoning
 * 
 */
public class AndGP extends GPNode {

	private static final long	serialVersionUID	= 1L;



	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		BooleanGP result = (BooleanGP) input;

		this.children[0].eval(state, thread, result, stack, individual, problem);

		// if the first child is true, then evaluate the second child
		// otherwise skip the evaluation of the second child and keep result as
		// being false
		if (result.value) {
			this.children[1].eval(state, thread, result, stack, individual, problem);
		}

		// This is debug code and should not be enabled in most production-style
		// experiments
		if (Debugger.DEBUG_NODE_VALUES)
			GPNodeDebug.debug(state, thread, result, this, "bool");

	}



	@Override
	public String toString() {
		return "and";
	}

}
