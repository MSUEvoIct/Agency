package ec.agency.eval;

import java.util.List;

import ec.Fitness;
import ec.simple.SimpleFitness;

public class ArithmaticMeanFitnessAggregator extends AbstractFitnessAggregator {
	private static final long serialVersionUID = 1L;

	@Override
	Fitness getAggregatedFitness(List<Fitness> samples) {
		SimpleFitness fit = new SimpleFitness();
		
		// use double for the aggregation for greater precision
		double totalFitness = 0;
		int numSamples = 0;
		for (Fitness f : samples) {
			SimpleFitness sf;
			try {
				sf = (SimpleFitness) f;
			} catch (Exception e) {
				String msg = "Only supports SimpleFitness";
				throw new RuntimeException(msg,e);
			}
			totalFitness += sf.fitness();
			numSamples++;
		}
		
		double aggregatedFitness = totalFitness/numSamples;
		fit.setFitness(null, (float) aggregatedFitness, false);
		return fit;
	}



}
