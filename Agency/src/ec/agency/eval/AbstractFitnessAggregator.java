package ec.agency.eval;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.util.Parameter;

public abstract class AbstractFitnessAggregator implements FitnessAggregator {
	private static final long serialVersionUID = 1L;

	Map<Individual, List<Fitness>> fitnessSamples = new IdentityHashMap<Individual, List<Fitness>>();

	@Override
	public void setup(EvolutionState evoState, Parameter base) {
		// Nothing to do
	}

	@Override
	public synchronized void addSample(Individual ind, Fitness fitness) {
		List<Fitness> samplesForIndividual = fitnessSamples.get(ind);
		if (samplesForIndividual == null) {
			samplesForIndividual = new ArrayList<Fitness>();
			fitnessSamples.put(ind, samplesForIndividual);
		}
		samplesForIndividual.add(fitness);
	}

	@Override
	public void updatePopulation(EvolutionState evoState) {
		
		int numSubpops = evoState.population.subpops.length;
		for (int i = 0; i < numSubpops; i++) {
			int numIndividuals = evoState.population.subpops[i].individuals.length;
			for (int j = 0; j < numIndividuals; j++) {
				Individual ind = evoState.population.subpops[i].individuals[j];
				ind.fitness = getAggregatedFitness(fitnessSamples.get(ind));
			}
		}
		
	}
	
	abstract Fitness getAggregatedFitness(List<Fitness> samples);
	
}
