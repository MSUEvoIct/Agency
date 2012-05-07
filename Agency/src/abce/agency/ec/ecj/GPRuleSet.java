package abce.agency.ec.ecj;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class GPRuleSet extends GPNode {

	@Override
	public String toString() {
		return "GPRuleSet";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		for (int i = 0; i < children.length; i++)
			this.children[i].eval(state, thread, input, stack, individual, problem);

	}

}
