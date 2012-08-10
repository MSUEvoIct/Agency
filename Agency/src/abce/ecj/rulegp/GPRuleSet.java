package abce.ecj.rulegp;


import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.rule.Rule;
import ec.rule.RuleSet;



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
