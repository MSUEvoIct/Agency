package abce.models.iteratedpd;

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

	boolean							inStep				= false;
	private static ThreadPoolExecutor threadPool;
	private static LinkedBlockingQueue<Runnable>	tasks				= new LinkedBlockingQueue<Runnable>();
	private static boolean waiting = false;
	
	@Override
	public void evaluatePopulation(EvolutionState state) {

		if (inStep)
			throw new RuntimeException("evaluatePopulaiton() is not reentrant, yet is being called recursively.");
		inStep = true;

		// Set up the threadpool to run the simulations. 
		Runtime r = Runtime.getRuntime();
		int numThreads = r.availableProcessors();
		threadPool = new ThreadPoolExecutor(numThreads, numThreads+2, 10, TimeUnit.SECONDS, tasks);
		threadPool.prestartAllCoreThreads();
		
		// Get parameters from the configuration file
		Parameter base = new Parameter("iteratedPD");
		final int numSteps = state.parameters.getInt(base.push("steps"), null);
		final int numEvaluations = state.parameters.getInt(base.push("evaluations"),null);
		final double payoffBothCooperate = state.parameters.getDouble(base.push("payoffBothCooperate"), null);
		final double payoffWinner = state.parameters.getDouble(base.push("payoffWinner"), null);
		final double payoffLoser = state.parameters.getDouble(base.push("payoffLoser"), null);
		final double payoffBothDefect = state.parameters.getDouble(base.push("payoffBothDefect"), null);
		
		if (!(payoffBothDefect > payoffLoser))
			throw new RuntimeException("Not a prisoners dilemmea game");
		if (!(payoffWinner > payoffBothCooperate))
			throw new RuntimeException("Not a prisoners dilemmea game");
		
		
		// Create our array of agents
		Subpopulation sp = state.population.subpops[0];
		int numIndividuals = sp.individuals.length;
		IteratedPDAgent[] agents = new IteratedPDAgent[numIndividuals];
		for (int i = 0; i < numIndividuals; i++) {
			IteratedPDAgent agent = new IteratedPDAgentGA( (BitVectorIndividual) sp.individuals[i]);
			agents[i] = agent;
		}
		
		// do the evaluations
		int simulationID = 0;
		for (int i = 0; i < numIndividuals; i++) {
			List<Integer> randomPairings = new ArrayList<Integer>();
			for (int j = 0; j < numEvaluations; j++) {
				randomPairings.add(state.random[0].nextInt(numIndividuals));
			}
			
			for (Integer opponentIndex : randomPairings) {
				
				if (opponentIndex == i)
					continue;
				
				IteratedPDSimulation sim = new IteratedPDSimulation(state.random[0].nextLong(), 
						agents[i],
						agents[opponentIndex],
						payoffBothCooperate,
						payoffWinner,
						payoffLoser,
						payoffBothDefect
						);
				
				sim.steps = numSteps;
				sim.generation = state.generation;
				sim.simulationID = (i * numIndividuals) + simulationID++;
				
				threadPool.execute(sim);
				
			}
			
		}
		
		threadPool.shutdown();
		try {
			int seconds = 0; // how long we've waited
			int waiting = 60; // update every 60 seconds
			int timeout = 1200; // 20 minutes
			while (seconds < timeout) {
				threadPool.awaitTermination(waiting, TimeUnit.SECONDS);
				seconds += waiting;
			}
			if (!threadPool.isTerminated()) {
				System.out.println("Could not evaluate population in under " + timeout + " seconds");
				System.exit(-1);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		// mark all the fitnesses
		for (int i = 0; i < numIndividuals; i++) {
			IteratedPDAgentGA agent = (IteratedPDAgentGA) agents[i];
			SimpleFitness fit = (SimpleFitness) agent.ind.fitness;
			
			// fitness is average earnings over all runs.
			fit.setFitness(state, (float) agent.getTotalRevenue()/numEvaluations, false);
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
