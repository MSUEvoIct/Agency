package ec.agency.eval;

import java.util.Iterator;
import java.util.Set;

import ec.EvolutionState;
import ec.Individual;

/**
 * The group creator should be intantiated each time it is to be used,
 * rather than keeping it around.  It's set up to function like an iterator
 * and has state related the specific set of individuals it is evaluating.
 * 
 * @author kkoning
 *
 */
public interface GroupCreator extends ec.Setup, Iterator<Set<Individual>> {
	/**
	 * This must be called at least once, or there will be no individuals to
	 * evaluate.  It may be called multiple times in the context of
	 * generating intergenerational statistics, but should throw an 
	 * exception if called after iteration through the list of groups has
	 * begun.
	 * 
	 * @param pop
	 */
	public void addPopulation(EvolutionState evoState);
}
