package abce.io.iterated.cournot;

import ec.Evaluator;
import ec.EvolutionState;
import ec.Subpopulation;
import ec.simple.SimpleFitness;
import ec.util.Parameter;
import ec.vector.BitVectorIndividual;

public class IteratedCournotEvaluator extends Evaluator {

	boolean							inStep				= false;

	@Override
	public void evaluatePopulation(EvolutionState state) {

		if (inStep)
			throw new RuntimeException("evaluatePopulaiton() is not reentrant, yet is being called recursively.");
		inStep = true;
		
		Parameter base = new Parameter("iteratedCournot");
		int numSteps = state.parameters.getInt(base.push("steps"), null);
		
		// We should always have BitVectorIndividuals, and always evaluate them in groups of 2
		Subpopulation sp = state.population.subpops[0];
		int numIndividuals = sp.individuals.length;
		int numSimulations = numIndividuals / 2;
		
		// create simulations
		IteratedCournotSimulation[] icsArray = new IteratedCournotSimulation[numSimulations];
		
		for(int i = 0; i < numIndividuals; i++) {
			if ((i % 2) == 0) {
				IteratedCournotAgentGA first = new IteratedCournotAgentGA((BitVectorIndividual) state.population.subpops[0].individuals[i]);
				IteratedCournotAgentGA second = new IteratedCournotAgentGA((BitVectorIndividual) state.population.subpops[0].individuals[i+1]);
				icsArray[i / 2] = new IteratedCournotSimulation(state.random[0].nextLong(), first, second);
				icsArray[i / 2].simulationID = i / 2;
				icsArray[i/2].generation = state.generation;
			}
		}
		
		for(int i = 0; i < icsArray.length; i++) {
			IteratedCournotSimulation ics = icsArray[i];
			ics.run(numSteps);
			SimpleFitness f1 = (SimpleFitness) ics.first.getFitness();
			f1.setFitness(state, (float) ics.first.getTotalRevenue(), false);
			ics.first.getIndividual().evaluated = true;
			SimpleFitness f2 = (SimpleFitness) ics.first.getFitness();
			f2.setFitness(state, (float) ics.second.getTotalRevenue(), false);
			ics.second.getIndividual().evaluated = true;
		}

		
		
		
		inStep = false;
		
	}

	@Override
	public boolean runComplete(EvolutionState state) {
		// an ideal individual is never found...
		return false;
	}

}
