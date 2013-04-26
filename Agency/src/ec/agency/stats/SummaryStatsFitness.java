package ec.agency.stats;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;

public class SummaryStatsFitness extends ec.Statistics {
	private static final long serialVersionUID = 1L;

	static int statisticsLog = 1; // stdout, taken from ecj src

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

		StringBuffer sb = new StringBuffer();
		sb.append("Subpop ");
		sb.append(subPopNum);
		sb.append(" fitnesses: ");
		sb.append(stats.getMin());
		sb.append("/");
		sb.append(stats.getMean());
		sb.append("(");
		sb.append(stats.getStandardDeviation());
		sb.append(")/");
		sb.append(stats.getMax());
		sb.append(", N=");
		sb.append(stats.getN());
		
		evoState.output.println(sb.toString(), statisticsLog);
		
//		evoState.output.println("Fitness summary statistics for subpopulation "
//				+ subPopNum + ", " + stats.getN() + " Individuals",
//				statisticsLog);
//
//		String minString = "Min: " + stats.getMin();
//		String meanDevString = "Mean(StdDev): " + stats.getMean() + "("
//				+ stats.getStandardDeviation() + ")";
//		String maxString = "Max: " + stats.getMax();
//
//		evoState.output.println(minString + ", " + meanDevString + ", "
//				+ maxString, statisticsLog);

	}
}
