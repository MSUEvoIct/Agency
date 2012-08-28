package abce.agency.ec.ecj;

import ec.Fitness;
import ec.Individual;

public interface FitnessListener {
	public void updateFitness(Individual ind, Double fit);
}
