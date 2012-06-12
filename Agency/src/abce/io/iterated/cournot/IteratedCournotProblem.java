package abce.io.iterated.cournot;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;

public class IteratedCournotProblem extends Problem implements SimpleProblemForm {

	@Override
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		SimpleFitness f = ((SimpleFitness) ind.fitness);
		
		
	}

}
