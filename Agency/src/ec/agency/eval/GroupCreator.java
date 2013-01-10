package ec.agency.eval;

import ec.EvolutionState;

/**
 * The group creator should be instantiated each time it is to be used,
 * rather than keeping it around.  It's set up to function like an iterator
 * and has state related the specific set of individuals it is evaluating.
 * 
 * @author kkoning
 *
 */
public interface GroupCreator extends ec.Setup, Iterable<EvaluationGroup> {
	/**
	 * This must be called at least once, or there will be no individuals to
	 * evaluate.  It may be called multiple times in the context of
	 * generating inter-generational statistics, but should throw an 
	 * exception if called after iteration through the list of groups has
	 * begun.
	 * 
	 * @param pop
	 */
	public void addPopulation(EvolutionState evoState);
}
