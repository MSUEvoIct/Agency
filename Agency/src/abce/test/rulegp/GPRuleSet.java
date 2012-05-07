package abce.test.rulegp;


import ec.*;
import ec.gp.*;
import ec.rule.*;



public class GPRuleSet extends RuleSet {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;



	public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
			final Problem problem) {
		for (Rule r : rules) {
			GPIndividual individual = ((GPRule) r).individual;
			for (int k = 0; k < individual.trees.length; k++) {
				individual.trees[k].child.eval(state, thread, input, stack, individual, problem);
			}
		}
	}

}
