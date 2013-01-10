package ec.agency.eval;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;

public interface FitnessAggregator extends ec.Setup {
	public void addSample(Individual ind, Fitness fitness);
	public void updatePopulation(EvolutionState evoState);
}
