package ec.agency.stats;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.vector.FloatVectorIndividual;

public class SummaryStatsVectorIndividual extends ec.Statistics {
	private static final long serialVersionUID = 1L;
	private static String separator = ", ";

	static int output = 0; // from ecj src

	@Override
	public void postEvaluationStatistics(EvolutionState state) {
		super.postEvaluationStatistics(state);
		// Iterate through the subpopulations, doing separate stats for each
		int numSubPops = state.population.subpops.length;
		for (int i = 0; i < numSubPops; i++) {
			perSubPopulationStats(state, i);
		}
	}

	private void perSubPopulationStats(EvolutionState evoState, int subpopnum) {
		Subpopulation subPop = evoState.population.subpops[subpopnum];

		Individual sampleInd = subPop.individuals[0];

		if (sampleInd instanceof FloatVectorIndividual) {
			evoState.output.println(
					"Summary Stats for VectorIndividuals, Subpopulation "
							+ subpopnum, output);
			floatVectorIndividuals(evoState, subpopnum);
		} else {

			evoState.output.println("Summary Stats for "
					+ sampleInd.getClass().getCanonicalName()
					+ " not supported.", output);
		}

	}

	private void floatVectorIndividuals(EvolutionState evoState, int subpopnum) {
		Subpopulation subPop = evoState.population.subpops[subpopnum];

		int numIndividuals = subPop.individuals.length;
		FloatVectorIndividual[] inds = new FloatVectorIndividual[numIndividuals];
		for (int i = 0; i < numIndividuals; i++) {
			inds[i] = (FloatVectorIndividual) subPop.individuals[i];
		}

		int genomeLength = ((FloatVectorIndividual) inds[0]).genomeLength();

		// Vector of statistics matches size of genome
		// Summary stats for each loci on the genome.
		SummaryStatistics[] stats = new SummaryStatistics[genomeLength];
		for (int i = 0; i < stats.length; i++) {
			stats[i] = new SummaryStatistics();
		}

		// Iterate through the individuals, dumping genome vector to
		// stats vector
		for (int i = 0; i < inds.length; i++) {
			float[] genome = inds[i].genome;
			for (int j = 0; j < genome.length; j++) {
				stats[j].addValue(inds[i].genome[j]);
			}
		}

		// Do minimums
		StringBuffer sbMins = new StringBuffer();
		sbMins.append("Mins: ");
		for (int i = 0; i < stats.length; i++) {
			sbMins.append(stats[i].getMin());
			if (i + 1 < stats.length)
				sbMins.append(separator);
		}
		evoState.output.println(sbMins.toString(), output);

		// Do mean/stddev
		StringBuffer sbMeans = new StringBuffer();
		sbMeans.append("Means(StdDev): ");
		for (int i = 0; i < stats.length; i++) {
			sbMeans.append(stats[i].getMean());
			sbMeans.append("(");
			sbMeans.append(stats[i].getStandardDeviation());
			sbMeans.append(")");
			if (i + 1 < stats.length)
				sbMeans.append(separator);
		}
		evoState.output.println(sbMeans.toString(), output);

		// Do max
		StringBuffer sbMax = new StringBuffer();
		sbMax.append("Max: ");
		for (int i = 0; i < stats.length; i++) {
			sbMax.append(stats[i].getMax());
			if (i + 1 < stats.length)
				sbMax.append(separator);
		}
		evoState.output.println(sbMax.toString(), output);

	}

}
