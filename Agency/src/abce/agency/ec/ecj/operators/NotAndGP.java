package abce.agency.ec.ecj.operators;


import abce.agency.ec.ecj.types.BooleanGP;
import abce.ecj.Debugger;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;



/**
 * Not returns true only if both of its children return false. If one of its
 * children returns true, it returns false.
 * 
 * @author kkoning
 * 
 */
public class NotAndGP extends GPNode {

	private static final long	serialVersionUID	= 1L;



	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		BooleanGP result = (BooleanGP) input;

		boolean retval = true;
		
		children[0].eval(state, thread, result, stack, individual, problem);
		if (result.value == true){
			children[1].eval(state, thread, result, stack, individual, problem);
			if (result.value == true){
				retval = false;
			}
		}
		
		result.value = retval;

		// This is debug code and should not be enabled in most production-style
		// experiments
		if (Debugger.DEBUG_NODE_VALUES)
			GPNodeDebug.debug(state, thread, input, this, "bool");

	}



	@Override
	public String toString() {
		return "nand";
	}

}
