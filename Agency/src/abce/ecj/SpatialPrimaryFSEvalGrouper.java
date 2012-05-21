package abce.ecj;


import ec.*;
import ec.spatial.*;



public class SpatialPrimaryFSEvalGrouper extends PrimaryFSEvalGrouper {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;



	@Override
	public EvaluationGroup next(final EvolutionState state, final int calling_thread, final int for_evaluation_thread) {

		Subpopulation spprime = state.population.subpops[primary_subpop];
		if (!(spprime instanceof Space)) {
			state.output.fatal("Spatial grouper requires the primary subpopulation [" + primary_subpop
					+ "] implement Space");
		}
		Space space = (Space) spprime;
		space.setIndex(calling_thread, next_ndx);

		if (next_ndx < spprime.individuals.length) {
			EvaluationGroup group = new EvaluationGroup(total_inds);
			Individual add_ind = state.population.subpops[primary_subpop].individuals[next_ndx];
			int add_subpop = primary_subpop;
			boolean add_evalfit = true;
			try {
				group.add(add_ind, add_subpop, add_evalfit);
			} catch (RuntimeException e) {
				e.printStackTrace();
				state.output.fatal("Unable to add individual to group.");
			}

			// For each co-source, pick a random individual
			for (int source = 0; source < subpop_sources.length; source++) {
				int spndx = subpop_sources[source];
				Subpopulation spop = state.population.subpops[spndx];
				for (int num = 0; num < subpop_sizes[source]; num++) {
					int rndx = (spndx == primary_subpop) ?
							space.getIndexRandomNeighbor(state, calling_thread, 1)
							:
							state.random[calling_thread].nextInt(spop.individuals.length);
					add_ind = spop.individuals[rndx];
					add_subpop = spndx;
					add_evalfit = false;
					try {
						group.add(add_ind, add_subpop, add_evalfit);
					} catch (RuntimeException e) {
						e.printStackTrace();
						state.output.fatal("Unable to add individual to group.");
					}
				}
			}
			next_ndx++;
			return group;
		} else {
			return null; // There are no more individuals in the primary
							// population to evaluate.
		}
	}
}
