package ec.agency.eval;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

public class RandomFlatGroupCreator implements GroupCreator {
	private static final long serialVersionUID = 1L;

	/**
	 * Contains the number of individuals that must be present in each set
	 * returned by this iterator.
	 */
	private int groupSize;
	private int roundsRemaining;
	private int samplesRemaining;
	private boolean dirty = false;

	private MersenneTwisterFast random;
	private List<Individual> allIndividuals;

	@Override
	public void setup(EvolutionState evoState, Parameter base) {
		this.random = new MersenneTwisterFast(evoState.random[0].nextLong());

		groupSize = evoState.parameters.getInt(base.push("groupSize"), null);
		roundsRemaining = evoState.parameters.getInt(base.push("rounds"), null);

		allIndividuals = new ArrayList<Individual>();
	}

	@Override
	public void addPopulation(EvolutionState evoState) {
		if (dirty)
			throw new RuntimeException(
					"Individuals added after iteration has started");

		// Put all the Individuals into a single group.
		for (int i = 0; i < evoState.population.subpops.length; i++) {
			for (int j = 0; j < evoState.population.subpops[i].individuals.length; j++) {
				allIndividuals.add(evoState.population.subpops[i].individuals[j]);
			}
		}

	}

	@Override
	public boolean hasNext() {
		if (roundsRemaining <= 0)
			return false;
		return true;
	}

	@Override
	public Set<Individual> next() {

		// Our first iteration
		if (!dirty) {
			dirty = true;
			samplesRemaining = allIndividuals.size();
			
		}

		Set<Individual> toReturn = new HashSet<Individual>();
		if (!hasNext())
			return null;

		for (int i = 0; i < groupSize; i++) {
			Individual ind = null;

			int j = 20;

			do {
				int randIndex = random.nextInt(allIndividuals.size());
				ind = allIndividuals.get(randIndex);
				j--;

				if (j < 0)
					throw new RuntimeException(
							"Group size too large in relation to population");

			} while (toReturn.contains(ind));

			toReturn.add(ind);

		}

		if (samplesRemaining == 0) {
			roundsRemaining--;
			samplesRemaining = allIndividuals.size();
		} else {
			samplesRemaining--;
		}

		return toReturn;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
