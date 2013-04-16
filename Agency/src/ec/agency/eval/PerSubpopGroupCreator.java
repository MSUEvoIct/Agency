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
 * @author kkoning
 *
 */
public class PerSubpopGroupCreator implements GroupCreator,
		Iterator<EvaluationGroup> {

	int numSubpopGroups;
	int[] groupSize;
	int totalGroupSize;
	int[] rounds;
	int samplesRemaining;
	boolean dirty = false;
	boolean firstRound;

	MersenneTwisterFast random;
	/** temporary, before start */
	List[] allIndividuals;
	/** warning, this is a NON-RECTANGULAR array of (different size) arrays */
	Individual[][] inds;
	int[] listPos;

	@Override
	public void setup(EvolutionState evoState, Parameter base) {
		this.random = new MersenneTwisterFast(evoState.random[0].nextLong());

		ParameterDatabase p = evoState.parameters;
		numSubpopGroups = p
				.getInt(new Parameter("breed.numSubpopGroups"), null);
		groupSize = new int[numSubpopGroups];
		allIndividuals = new List[numSubpopGroups];
		rounds = new int[numSubpopGroups];
		listPos = new int[numSubpopGroups];
		
		for (int i = 0; i < numSubpopGroups; i++) {
			allIndividuals[i] = new ArrayList<Individual>();
			groupSize[i] = p.getInt(base.push("groupSize." + i), null);
			totalGroupSize += groupSize[i];
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
		if (listPos[spg] >= inds[spg].length) {
			// randomize
			Individual tmp;
			for (int i = 0; i < inds[spg].length; i++) {
				int randInd = random.nextInt(inds[spg].length);
				tmp = inds[spg][randInd];
				inds[spg][randInd] = inds[spg][i];
				inds[spg][i] = tmp;
			}
			// reset list pos
			listPos[spg] = 0;
			// decrement rounds for that spopgroup
			rounds[spg]--;
		}
		
		// reset has occurred if necessary
		Individual toReturn = inds[spg][listPos[spg]++];
		return toReturn;
	}
	

	@Override
	public EvaluationGroup next() {
		EvaluationGroup toReturn = new EvaluationGroup();
		toReturn.individuals = new ArrayList<Individual>();
		
		// Our first iteration
		if (!dirty) {
			// move everything to the fixed-length arrays
			// (did this so randomizing order would be easier)
			inds = new Individual[allIndividuals.length][];
			for (int i = 0; i < allIndividuals.length; i++) {
				int numInds = allIndividuals[i].size();
				inds[i] = new Individual[numInds];
				for (int j = 0; j < numInds; j++) {
					inds[i][j] = (Individual) allIndividuals[i].get(j);
				}
			}
			
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
					allIndividuals[group].add(ind);
				}

			} else {
				throw new RuntimeException(this.getClass().getName()
						+ " requires ScalableSubpopulations");
			}
			
		}
	}

}
