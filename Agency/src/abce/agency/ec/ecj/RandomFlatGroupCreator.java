package abce.agency.ec.ecj;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

public class RandomFlatGroupCreator implements GroupCreator {
	private static final long serialVersionUID = 1L;

	private int groupSize;
	private int roundsRemaining;
	private int samplesRemaining;

	private MersenneTwisterFast random;

	private List<Individual> allIndividuals = new ArrayList<Individual>();

	@Override
	public void setup(EvolutionState evoState, Parameter base) {
		this.random = evoState.random[0];

		// Put all the Individuals into a single group.
		Population pop = evoState.population;
		for (int i = 0; i < pop.subpops.length; i++) {
			for (int j = 0; j < pop.subpops[i].individuals.length; j++) {
				allIndividuals.add(pop.subpops[i].individuals[j]);
			}
		}
		
		ParameterDatabase pd = evoState.parameters;
		
		groupSize = pd.getInt(base.push("groupSize"), null);
		roundsRemaining = pd.getInt(base.push("rounds"), null);
		samplesRemaining = allIndividuals.size();
	}

	@Override
	public boolean hasNext() {
		if (roundsRemaining <= 0)
			return false;
		return true;
	}

	@Override
	public Set<Individual> next() {
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
