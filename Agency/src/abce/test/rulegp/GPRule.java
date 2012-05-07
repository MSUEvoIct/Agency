package abce.test.rulegp;


import ec.*;
import ec.gp.*;
import ec.rule.*;



/**
 * GPRule is responsible for creating, storing, manipulating, and evaluating a
 * single GPTree as part of a GPRuleSet.
 * 
 * @author ruppmatt
 * 
 */
public class GPRule extends Rule {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public GPIndividual			individual;



	@Override
	public int hashCode() {
		return individual.hashCode();
	}



	@Override
	public void reset(EvolutionState state, int thread) {
		// TODO Auto-generated method stub

	}



	@Override
	public int compareTo(Object o) {
		int rhs = ((GPIndividual) o).hashCode();
		int lhs = individual.hashCode();
		if (lhs > rhs) {
			return 1;
		} else if (lhs < rhs) {
			return -1;
		} else {
			return 0;
		}
	}

}
