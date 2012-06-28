package abce.agency.ec.ecj;


import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.gp.GPIndividual;



/**
 * This crossover pipeline follows the same logical layout its spatial
 * counterpart but it select an individual for crossover at random from the
 * *entire* population.
 * 
 * @author ruppmatt
 * 
 */
public class MockSpatialCrossoverPipeline extends SpatialCrossoverPipeline {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;



	/**
	 * The bulk of this method is grafted from koza.CrossoverPipeline. The main
	 * change is that the current individual being evaluated must be used as the
	 * first parent individual; the second individual must be a random neighbor.
	 */
	@Override
	public int produce(final int min,
			final int max,
			final int start,
			final int subpopulation,
			final Individual[] inds,
			final EvolutionState state,
			final int thread)

	{

		Subpopulation this_subpop = state.population.subpops[subpopulation];

		// how many individuals should we make?
		int n = typicalIndsProduced();

		// should we bother?
		// DO produce children from source -- we've not done so already
		if (!state.random[thread].nextBoolean(likelihood))
			return reproduce(n, start, subpopulation, inds, state, thread, true);

		// Process the upstream source for this node
		// This will set our parent[0]
		sources[0].produce(1, 1, 0, subpopulation, parents, state, thread);

		// Retrieve a *ANY* individual (may have been purged by another
		// thread but still available to ours)
		int neighbor_ndx = state.random[thread].nextInt(state.population.subpops[subpopulation].individuals.length);
		if (neighbor_ndx < 0) {
			state.output.fatal("SpatialCrossoverPipeline could not find a neighbor.");
		}
		parents[1] = (GPIndividual) this_subpop.individuals[neighbor_ndx].clone();

		// Hold the products before placing them.
		GPIndividual products[] = new GPIndividual[1];

		// Actually perform the crossover
		doCrossOver(state, subpopulation, thread, products);

		// Place the product tree at the start location
		inds[start] = products[0];

		return 1;

	}

}
