package ec.agency.prisonersdilemma;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ec.Evaluator;
import ec.EvolutionState;
import ec.Subpopulation;
import ec.simple.SimpleFitness;
import ec.util.Parameter;
import ec.vector.BitVectorIndividual;

public class IteratedPDEvaluator extends Evaluator {

	boolean inStep = false;

	@Override
	public void evaluatePopulation(EvolutionState state) {

		if (inStep)
			throw new RuntimeException("evaluatePopulaiton() is not reentrant, yet is being called recursively.");
		inStep = true;

		// Get parameters from the configuration file
		Parameter base = new Parameter("iteratedPD");
		final int numSteps = state.parameters.getInt(base.push("steps"), null);
		final int numEvaluations = state.parameters.getInt(base.push("evaluations"),null);
		final double payoffBothCooperate = state.parameters.getDouble(base.push("payoffBothCooperate"), null);
		final double payoffWinner = state.parameters.getDouble(base.push("payoffWinner"), null);
		final double payoffLoser = state.parameters.getDouble(base.push("payoffLoser"), null);
		final double payoffBothDefect = state.parameters.getDouble(base.push("payoffBothDefect"), null);
		
		if (!(payoffBothDefect > payoffLoser))
			throw new RuntimeException("Not a prisoner's dilemmea game");
		if (!(payoffWinner > payoffBothCooperate))
			throw new RuntimeException("Not a prisoner's dilemmea game");
		
		
		// Create our array of agents
		Subpopulation sp = state.population.subpops[0];
		int numIndividuals = sp.individuals.length;
		IteratedPDAgent[] agents = new IteratedPDAgent[numIndividuals];
		for (int i = 0; i < numIndividuals; i++) {
			IteratedPDAgent agent = new IteratedPDAgentGA( (BitVectorIndividual) sp.individuals[i]);
			agents[i] = agent;
		}
		
		double[] totalEarnings = new double[numIndividuals];
		int[] timesEvaluated = new int[numIndividuals];
		
		// do the evaluations
		int simulationID = 0;
		for (int i = 0; i < numIndividuals; i++) {
			List<Integer> randomPairings = new ArrayList<Integer>();
			for (int j = 0; j < numEvaluations; j++) {
				randomPairings.add(state.random[0].nextInt(numIndividuals));
			}
			
			for (Integer opponentIndex : randomPairings) {
				
				// why can't it compete against itself?  no state...
//				if (opponentIndex == i)
//					continue;
				
				IteratedPDSimulation sim = new IteratedPDSimulation(state.random[0].nextLong(), 
						numSteps,
						agents[i],
						agents[opponentIndex],
						payoffBothCooperate,
						payoffWinner,
						payoffLoser,
						payoffBothDefect
						);
				
				sim.generation = state.generation;
				sim.simulationID = (i * numIndividuals) + simulationID++;

				// Actually run the simulation
				sim.run();
				
				
				// Track the fitness of each agent
				// TODO:  Will need to change this if non-GA agents are included
				// in the evaluation
				totalEarnings[i] += sim.earningsFirst;
				totalEarnings[opponentIndex] += sim.earningsSecond;
				timesEvaluated[i]++;
				timesEvaluated[opponentIndex]++;
			}
			
		}
		
		// mark all the fitnesses
		for (int i = 0; i < numIndividuals; i++) {
			IteratedPDAgentGA agent = (IteratedPDAgentGA) agents[i];
			SimpleFitness fit = (SimpleFitness) agent.ind.fitness;
			
			// fitness is average earnings over all runs.
			fit.setFitness(state, (float) totalEarnings[i]/timesEvaluated[i], false);
			agent.ind.evaluated = true;
		}
		
		inStep = false;
		
	}

	@Override
	public boolean runComplete(EvolutionState state) {
		// an ideal individual is never found...
		return false;
	}

}
