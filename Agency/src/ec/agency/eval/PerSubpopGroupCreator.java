package ec.agency.eval;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.agency.ScalableSubpopulation;
import ec.agency.util.IdentitySet;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * AgencyEvaluator instantiates a new GroupCreator for each generation.  After it does so,
 * it calls addPopulation to put individuals into the pool to be evaluated.  This is done
 * so that individuals from multiple generations can be evaluated together.  For this reason,
 * when we add our first individuals, we don't know how many tota
 * 
 * @author kkoning
 *
 */
public class PerSubpopGroupCreator implements GroupCreator,
		Iterator<EvaluationGroup> {

	int numSubpopGroups;
	int[] groupSize;
	int[] rounds;
	int samplesRemaining;
	boolean dirty = false;
	boolean firstRound;

	MersenneTwisterFast random;
	
	List<Individual>[] allIndsPerSubpopGroup;
	
	/** warning, this is a NON-RECTANGULAR array of (different size) arrays */
	int[] listPos;

	
	/* 
	 * AgencyEvaluator creates a new instantiation of the GroupCreator each
	 * generation, and the group creation component is single-threaded.
	 * 
	 * (non-Javadoc)
	 * @see ec.Setup#setup(ec.EvolutionState, ec.util.Parameter)
	 */
	@Override
	public void setup(EvolutionState evoState, Parameter base) {
		this.random = new MersenneTwisterFast(evoState.random[0].nextLong());

		ParameterDatabase p = evoState.parameters;
		numSubpopGroups = p
				.getInt(new Parameter("breed.numSubpopGroups"), null);
		groupSize = new int[numSubpopGroups];
		rounds = new int[numSubpopGroups];
		listPos = new int[numSubpopGroups];
		allIndsPerSubpopGroup = new List[numSubpopGroups];
		
		for (int i = 0; i < numSubpopGroups; i++) {
			allIndsPerSubpopGroup[i] = new ArrayList<Individual>();
			groupSize[i] = p.getInt(base.push("groupSize." + i), null);
			rounds[i] = p.getInt(base.push("rounds." + i), null);
		}
	}

	@Override
	public Iterator<EvaluationGroup> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		for (int i = 0; i < rounds.length; i++) 
			if (rounds[i] >= 0)
				return true;
		return false;
	}
	
	private Individual getInd(int spg) {
		if (listPos[spg] >= allIndsPerSubpopGroup[spg].size()) {
			// randomize
			Individual tmp;
			for (int i = 0; i < allIndsPerSubpopGroup[spg].size(); i++) {
				int randInd = random.nextInt(allIndsPerSubpopGroup[spg].size());
				tmp = allIndsPerSubpopGroup[spg].get(randInd);
				allIndsPerSubpopGroup[spg].set(randInd, allIndsPerSubpopGroup[spg].get(i));
				allIndsPerSubpopGroup[spg].set(i, tmp);
			}
			// reset list pos
			listPos[spg] = 0;
			// decrement rounds for that spopgroup
			rounds[spg]--;
		}
		
		// reset has occurred if necessary
		Individual toReturn = allIndsPerSubpopGroup[spg].get(listPos[spg]++);
		return toReturn;
	}
	

	@Override
	public EvaluationGroup next() {
		EvaluationGroup toReturn = new EvaluationGroup();
		toReturn.individuals = new ArrayList<Individual>();
		
		// Our first iteration
		if (!dirty) {
			dirty = true;
		}

		if (!hasNext())
			return null;

		for (int i = 0; i < groupSize.length; i++) {
			for (int j = 0; j < groupSize[i]; j++) {
				toReturn.individuals.add(getInd(i));
			}
		}

		
		return toReturn;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addPopulation(EvolutionState evoState) {
		if (dirty)
			throw new RuntimeException(
					"Individuals added after iteration has started");

		// Put all the Individuals into a single group.
		Subpopulation[] subpops = evoState.population.subpops;

		for (int i = 0; i < subpops.length; i++) {
			if (subpops[i] instanceof ScalableSubpopulation) {
				ScalableSubpopulation ss = (ScalableSubpopulation) subpops[i];
				int group = ss.getSubpopulationGroup();
				for (Individual ind : subpops[i].individuals) {
					allIndsPerSubpopGroup[group].add(ind);
				}

			} else {
				throw new RuntimeException(this.getClass().getName()
						+ " requires ScalableSubpopulations");
			}
			
		}
	}

}
