package abce.models.io.iterated.cournot;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ec.Evaluator;
import ec.EvolutionState;
import ec.Subpopulation;
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
			ics.steps = numSteps;
			ics.run();
		}		
		
		inStep = false;
		
	}

	@Override
	public boolean runComplete(EvolutionState state) {
		// an ideal individual is never found...
		return false;
	}

}
