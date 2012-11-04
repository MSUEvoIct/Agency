package abce.agency.ec.ecj;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.Subpopulation;
import ec.simple.SimpleBreeder;
import ec.util.Parameter;

/**
 * 
 * TODO: This overrides the normal elitism behavior. If elitism is required, it
 * will need to be re-implemented (i.e., re-integrated) here. For now, I'm not
 * accounting for it.
 * 
 * @author kkoning
 * 
 */
public class AgencyBreeder extends SimpleBreeder {
	private static final long serialVersionUID = 1L;

	/**
	 * Breeding of new individuals is broken up into separate Runnables and fed
	 * through a task queue. This controlls typical/maximum number of
	 * inidivudals each Runnable is responsible for producing. In other words,
	 * it will either be this number or a smaller number representing the
	 * individuals left in that subpopulation.
	 */
	protected int breedChunkSize = 100;

	@Override
	public Population breedPopulation(EvolutionState state) {

		// Get a new blank population (i.e., without Individuals)
		Population newPop = (Population) state.population.emptyClone();

		// How large are the new subpopulations going to be?
		int numSubpops = state.population.subpops.length;
		for (int i = 0; i < numSubpops; i++)
			newPop.subpops[i].individuals = new Individual[newSubpopSize(state, i)];

		// TODO: Parallelize this with a ThreadPoolExecutor.
		
		// Create chunks to breed and breed them.
		
		
		
		return newPop;
	}

	/**
	 * @param state
	 * @param subPop
	 * @return the number of individuals
	 */
	protected int newSubpopSize(EvolutionState state, int subPop) {
		Subpopulation sp = state.population.subpops[subPop];

		// if this isn't a scalable subpopulation, don't change the size.
		// if (!(sp instanceof ScalableSubpopulation))
		return sp.individuals.length;

		/*
		 * TODO: Actually do the scaling. Try it without this feature initially,
		 * or with a simple version of it like adding one each generation or
		 * something.
		 */

		
		
	}

	@Override
	public void setup(EvolutionState state, Parameter base) {
		// TODO Auto-generated method stub
		super.setup(state, base);

		/*
		 * We'll need some parameters to help determine the variable sizes of
		 * populations
		 */

	}

}
