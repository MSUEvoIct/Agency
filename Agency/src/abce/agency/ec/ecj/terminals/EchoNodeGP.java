package abce.agency.ec.ecj.terminals;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

/**
 * There are no return types for this node; it tends to be used when there is a terminal needed but the type requires more than one node beneath it.
 * @author ruppmatt
 *
 */
public class EchoNodeGP extends GPNode{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void eval(EvolutionState state, int thread, GPData data, ADFStack adf,
			GPIndividual ind, Problem prob) {
		// Do nothing
		
	}

	@Override
	public String toString() {
		return "Echo";
	}

}
