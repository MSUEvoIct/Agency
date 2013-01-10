package ec.agency.stats;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;

public class SummaryStatsFitness extends ec.Statistics {
	private static final long serialVersionUID = 1L;

	static int statisticsLog = 0; // stdout, taken from ecj src

	@Override
	public void postEvaluationStatistics(EvolutionState state) {
		super.postEvaluationStatistics(state);

		// Iterate through the subpopulations, doing separate stats for each
		int numSubPops = state.population.subpops.length;
		for (int i = 0; i < numSubPops; i++) {
			perSubPopulationStats(state, i);
		}
	}

	private void perSubPopulationStats(EvolutionState evoState, int subPopNum) {

		SummaryStatistics stats = new SummaryStatistics();
		Subpopulation subPop = evoState.population.subpops[subPopNum];

		int numIndividuals = subPop.individuals.length;
		for (int i = 0; i < numIndividuals; i++) {
			Individual ind = subPop.individuals[i];
			float indFitness = ind.fitness.fitness();
			stats.addValue(indFitness);
		}

		// TODO, use ECJ's output facility?
		evoState.output.println("Fitness summary statistics for subpopulation "
				+ subPopNum + ", " + stats.getN() + " Individuals",
				statisticsLog);

		String minString = "Min: " + stats.getMin();
		String meanDevString = "Mean(StdDev): " + stats.getMean() + "("
				+ stats.getStandardDeviation() + ")";
		String maxString = "Max: " + stats.getMax();

		evoState.output.println(minString + ", " + meanDevString + ", "
				+ maxString, statisticsLog);

	}
}
