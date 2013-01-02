package ec.agency;


import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.agency.eval.EvaluationGroup;
import ec.agency.eval.EvaluationGrouper;
import ec.util.Parameter;



/**
 * This EvaluationGrouper evaluates each individual in the primary subpopulation
 * individually against a random (but fixed-size) set of individuals chosen from
 * the entire Population.
 * 
 * Fitnesses are *only* set for individuals in the primary subpopulation.
 * 
 * @author ruppmatt
 * 
 */
public class PrimaryFSEvalGrouper extends EvaluationGrouper {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	// The ID of the primary subpopulation
	public int					primary_subpop;

	// Which subpopulations to draw random individuals from
	public int[]				subpop_sources;

	// The sample size from each subpopulation
	public int[]				subpop_sizes;

	// The total number of individuals being evaluated
	public int					total_inds;

	// Non-prototypic, the next index in the primary subpopulation to evaluate
	public int					next_ndx			= 0;

	public static final String	P_PRIMARY_SP		= "primary_subpop";
	public static final String	P_NUM_SOURCES		= "num_sources";
	public static final String	P_SOURCE_SP			= "source";
	public static final String	P_SOURCE_SIZE		= "size";
	public static final int		DEFAULT_WEIGHT		= 1;



	@Override
	public void setup(final EvolutionState state, Parameter base) {
		super.setup(state, base);

		Parameter defbase = defaultBase();

		// Establish which subpopulation will have all its individuals
		// independently evaluated
		primary_subpop = state.parameters.getInt(base.push(P_PRIMARY_SP), defbase.push(P_PRIMARY_SP));

		// Establish the number of subpopulation sources we will be using
		int num_sources = state.parameters.getInt(base.push(P_NUM_SOURCES), defbase.push(P_NUM_SOURCES));
		subpop_sources = new int[num_sources];
		subpop_sizes = new int[num_sources];

		total_inds = 1;
		for (int ndx = 0; ndx < subpop_sources.length; ndx++) {
			String sndx = String.valueOf(ndx);
			String sparam = P_SOURCE_SP + "." + sndx;
			int sp = state.parameters.getInt(base.push(sparam), defbase.push(sparam));
			subpop_sources[ndx] = sp;
			String szparam = sparam + "." + P_SOURCE_SIZE;
			if (state.parameters.exists(base.push(szparam), defbase.push(sparam))) {
				subpop_sizes[ndx] = state.parameters.getInt(base.push(szparam), defbase.push(szparam));
			} else {
				subpop_sizes[ndx] = 1;
			}
			total_inds += subpop_sizes[ndx];
		}
	}



	@Override
	public Object clone() {
		PrimaryFSEvalGrouper cl = (PrimaryFSEvalGrouper) (super.clone());
		cl.reset();
		return cl;

	}



	@Override
	public void reset() {
		next_ndx = 0;
	}



	@Override
	public void prepareGrouper(EvolutionState state, int calling_thread) {
		reset();
	}



	/**
	 * For each individual in the primary subpopulation, create an
	 * Evaluation Group. Only the individual in the primary subpopulation being
	 * focused on has its fitness evaluated. All peers in the group are randomly
	 * selected by the configuration settings and will not have their fitnesses
	 * re-evaluated.
	 * 
	 * @param state
	 * @param threadnum
	 * @return
	 *         an EvaluationGroup or null if empty
	 */
	@Override
	public EvaluationGroup next(final EvolutionState state, final int calling_thread, final int for_evaluation_thread) {

		Subpopulation spprime = state.population.subpops[primary_subpop];
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
					int rndx = state.random[calling_thread].nextInt(spop.individuals.length);
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
