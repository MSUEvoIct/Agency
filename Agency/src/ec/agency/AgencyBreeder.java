package ec.agency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ec.EvolutionState;
import ec.Population;
import ec.Subpopulation;
import ec.simple.SimpleBreeder;
import ec.util.Parameter;

/**
 * 
 * The purpose of this breeder is to scale the subpopulation sizes in a fitness-
 * proportional way. We should probably reference documentation here that
 * explains why this is necessary and the impact it is expected to have on
 * outcomes.
 * 
 * @author kkoning
 * 
 */
public class AgencyBreeder extends SimpleBreeder {
	private static final long serialVersionUID = 1L;

	private static final String P_tournamentSize = "tournamentSize";
	private static final String P_numSubpopGroups = "numSubpopGroups";
	private static final String P_changeWeight = "changeWeight";
	private static final String P_size = "size";

	int numSubpopGroups;
	int tournamentSize;
	/**
	 * The # of individuals in each subpopulation group; as specified in the
	 * configuration file.
	 */
	int[] groupSizes;
	float changeWeight;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		tournamentSize = state.parameters.getInt(base.push(P_tournamentSize),
				null);
		numSubpopGroups = state.parameters.getInt(base.push(P_numSubpopGroups),
				null);
		groupSizes = new int[numSubpopGroups];

		for (int i = 0; i < numSubpopGroups; i++) {
			groupSizes[i] = state.parameters.getInt(base
					.push(P_numSubpopGroups).push("" + i).push(P_size), null);
		}

		changeWeight = state.parameters.getFloat(base.push(P_changeWeight),
				null);
		if (changeWeight < 0)
			throw new RuntimeException("breed.changeWeight must be in (0-1].");
		if (changeWeight >= 1)
			throw new RuntimeException("breed.changeWeight must be in (0-1].");

	}

	@Override
	public Population breedPopulation(EvolutionState state) {
		// How large are the new subpopulations going to be?
		int[] subpopSizes = newSubpopSizes(state);

		//
		for (int i = 0; i < subpopSizes.length; i++) {
			Subpopulation s = state.population.subpops[i];
			if (s instanceof ScalableSubpopulation) {
				ScalableSubpopulation ss = (ScalableSubpopulation) s;
				ss.setTargetSize(subpopSizes[i]);
			}
		}

		return super.breedPopulation(state);
	}

	/**
	 * @param state
	 * @param subPop
	 * @return the number of individuals
	 */
	protected int[] newSubpopSizes(EvolutionState state) {
		int numSubpops = state.population.subpops.length;

		int[] existingSize = new int[numSubpops];
		int[] targetSize = new int[numSubpops];

		// Populate existing sizes
		for (int i = 0; i < numSubpops; i++) {
			existingSize[i] = state.population.subpops[i].individuals.length;
		}

		// Populate target sizes
		// allocate minimums first, subtract sub of minimums from total
		int[] remainingInds = groupSizes.clone();
		for (int i = 0; i < numSubpopGroups; i++) { // i = subpop group
			for (int j = 0; j < numSubpops; j++) { // j = subpop
				Subpopulation s = state.population.subpops[j];
				if (s instanceof ScalableSubpopulation) {
					ScalableSubpopulation ss = (ScalableSubpopulation) s;
					int thisSpopGroup = ss.getSubpopulationGroup();
					if (thisSpopGroup == i) {
						targetSize[j] += ss.getMinSize();
						remainingInds[i] -= ss.getMinSize();
					}
				}
			}
		}

		// allocate remaining slots with tournament selection
		// TODO: Make this modular?
		for (int i = 0; i < numSubpopGroups; i++) { // i = subpop group
			List<SimpleInd> collapsedInds = collectInds(state, i);

			while (remainingInds[i] > 0) {
				List<SimpleInd> tournamentMembers = new ArrayList<SimpleInd>();
				for (int j = 0; j < tournamentSize; j++) { // j = slot in
															// tournament
					int numInds = collapsedInds.size();
					int randIndex = state.random[0].nextInt(numInds);
					tournamentMembers.add(collapsedInds.get(randIndex));
				}
				SimpleInd si = Collections.max(tournamentMembers);
				int luckySubpop = si.subPop;
				targetSize[luckySubpop]++;
				remainingInds[i]--;
			}

		}

		// Average the existing and targetsizes
		int[] finalSizes = new int[numSubpops];
		float stayWeight = 1 - changeWeight;
		for (int i = 0; i < numSubpops; i++) {
			finalSizes[i] = (int) (existingSize[i] * stayWeight + targetSize[i]
					* changeWeight);
		}

		return finalSizes;

	}

	private class SimpleInd implements Comparable<SimpleInd> {

		int subPop;
		float fitness;

		@Override
		public int compareTo(SimpleInd o) {
			if (fitness > o.fitness)
				return 1;
			if (fitness < o.fitness)
				return -1;
			return 0;
		}

	}

	private List<SimpleInd> collectInds(EvolutionState state, int subpopGroup) {
		List<SimpleInd> toReturn = new ArrayList<SimpleInd>();
		int numSubops = state.population.subpops.length;
		for (int i = 0; i < numSubops; i++) {
			Subpopulation s = state.population.subpops[i];
			if (s instanceof ScalableSubpopulation) {
				ScalableSubpopulation ss = (ScalableSubpopulation) s;
				int thisGroup = ss.getSubpopulationGroup();
				if (thisGroup == subpopGroup) {
					// Add everyone
					int numInds = s.individuals.length;
					for (int j = 0; j < numInds; j++) {
						SimpleInd si = new SimpleInd();
						si.subPop = i;
						si.fitness = s.individuals[j].fitness.fitness();
						toReturn.add(si);
					}
				}
			}
		}
		return toReturn;

	}

}
