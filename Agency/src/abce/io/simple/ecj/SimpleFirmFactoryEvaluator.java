package abce.io.simple.ecj;


import java.lang.reflect.*;
import java.util.concurrent.*;

import abce.agency.ec.*;
import abce.agency.ec.ecj.*;
import abce.agency.firm.*;
import abce.io.simple.*;
import ec.*;
import ec.simple.*;
import ec.util.*;



public class SimpleFirmFactoryEvaluator extends Evaluator {

	private static final long	serialVersionUID	= 1L;
	boolean						inStep				= false;



	@Override
	public void evaluatePopulation(EvolutionState state) {

		// Protect from recursion
		if (inStep)
			throw new RuntimeException("evaluatePopulaiton() is not reentrant, yet is being called recursively.");

		inStep = true;

		// Collect configuration information
		Parameter base = new Parameter("agency");
		int numChunks = state.parameters.getInt(base.push("chunks"), null);
		int numSteps = state.parameters.getInt(base.push("steps"), null);
		String config_path = state.parameters.getString(base.push("config"), null);

		// Build the domain models
		ECSimpleMarketSimulation[] models = new ECSimpleMarketSimulation[numChunks];
		for (int i = 0; i < numChunks; i++) {
			int seed = state.random[0].nextInt();
			models[i] = new ECSimpleMarketSimulation(seed, config_path, i, state.generation);
		}

		// Populate the domain models and bind representations
		try {
			populateModels(state, models);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Execute the domain models
		executeModels(state, models);

		// Evaluate the fitnesses of the individuals
		SimpleProblemForm spf = (SimpleProblemForm) p_problem.clone();
		for (Subpopulation sp : state.population.subpops) {
			for (Individual individual : sp.individuals) {
				if (((AgencyGPIndividual) individual).getAgent() == null) {
					System.err.println("Unable to evaluate individual not bound to an agent.");
					System.exit(1);
				}
				spf.evaluate(state, individual, 0, 0);
			}
		}

		inStep = false;
	}



	/**
	 * Populate the models with agents bound to GPIndividuals, associated with
	 * StimulusResponse objects
	 * 
	 * @param state
	 * @param models
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	protected void populateModels(EvolutionState state, ECSimpleMarketSimulation[] models)
			throws SecurityException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
			IllegalArgumentException, InstantiationException, IllegalAccessException {
		for (int sp_ndx = 0; sp_ndx < state.population.subpops.length; sp_ndx++) {
			Subpopulation sp = state.population.subpops[sp_ndx];
			int subpop_size = sp.individuals.length;
			Constructor<ECJEvolvableAgent> agent_constructor = findAgentConstructor(state, sp_ndx);
			randomizeSubpopulation(state, sp);
			Class<? extends StimulusResponse>[] sr_classes = getSRClasses(state, sp_ndx);
			int num_agents = subpop_size / models.length;
			int ind_ndx = 0;
			for (int model = 0; model < models.length; model++) {
				for (int a = 0; a < num_agents; a++) {
					ECJEvolvableAgent agent = setupAgent(agent_constructor, models[model]);
					bindSR(state, sp_ndx, ind_ndx, agent, sr_classes);
					ind_ndx++;
				}
			}

		}
	}



	/**
	 * Retrieve the StimulusResponse classes associated with each
	 * species
	 * 
	 * @param state
	 * @param sp_ndx
	 * @return
	 * @throws ClassNotFoundException
	 */
	protected static Class<? extends StimulusResponse>[] getSRClasses(EvolutionState state, int sp_ndx)
			throws ClassNotFoundException {
		Parameter species_base = new Parameter("pop").push("subpop").push(Integer.toString(sp_ndx)).push("species")
				.push("ind");
		int num_trees = state.parameters.getInt(species_base.push("numtrees"), null);
		@SuppressWarnings("unchecked")
		Class<? extends StimulusResponse>[] sr_classes = (Class<? extends StimulusResponse>[]) new Class<?>[num_trees];
		for (int tree_ndx = 0; tree_ndx < num_trees; tree_ndx++) {
			Parameter tree_base = species_base.push("tree").push(Integer.toString(sp_ndx));
			String sr_name = state.parameters.getString(tree_base.push("sr"), null);
			@SuppressWarnings("unchecked")
			Class<? extends StimulusResponse> sr_class = (Class<? extends StimulusResponse>) Class.forName(sr_name);
			sr_classes[tree_ndx] = sr_class;
		}
		return sr_classes;

	}



	/**
	 * Perform any work to setup individual agents
	 * 
	 * @param constructor
	 *            The constructor that creates the agent
	 * @param model
	 *            The model the agent belongs to
	 * @return
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	protected static ECJEvolvableAgent setupAgent(Constructor<ECJEvolvableAgent> constructor,
			ECSimpleMarketSimulation model)
			throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		ECJEvolvableAgent agent = constructor.newInstance();
		if (agent instanceof ECJSimpleFirm) {
			model.setupFirm((Firm) agent);
		}
		return agent;
	}



	/**
	 * Bind a GP individual to each agent
	 * 
	 * @param state
	 *            The Evolution State
	 * @param subpop_ndx
	 *            The subpopulation to bind individuals from
	 * @param ind_ndx
	 *            The index of the individual to find
	 * @param agent
	 *            The agent to bind
	 * @param sr_classes
	 */
	protected static void bindSR(EvolutionState state, int subpop_ndx, int ind_ndx, ECJEvolvableAgent agent,
			Class<? extends StimulusResponse>[] sr_classes) {
		AgencyGPIndividual ind = (AgencyGPIndividual) state.population.subpops[subpop_ndx].individuals[ind_ndx];
		agent.register(state, ind, sr_classes.clone());
	}



	/**
	 * Find the appropriate constructor for each agent
	 * 
	 * @param state
	 *            The evolution state
	 * @param subpop_number
	 *            The index of the subpopulation
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings("unchecked")
	protected static Constructor<ECJEvolvableAgent> findAgentConstructor(EvolutionState state, int subpop_number)
			throws ClassNotFoundException, SecurityException, NoSuchMethodException {
		// and set up the ability to create those agents.
		String agentClassName = null;
		Class<?> agentClass = null;
		Constructor<ECJEvolvableAgent> agentConstructor = null;
		Parameter p = new Parameter("pop").push("subpop").push(Integer.toString(subpop_number)).push("species")
				.push("agent");
		agentClassName = state.parameters.getString(p, null);
		agentClass = Class.forName(agentClassName);
		System.err.println("Class to find constructor for: " + agentClass.getCanonicalName());
		agentConstructor = (Constructor<ECJEvolvableAgent>) agentClass.getConstructor((Class<?>[]) null);
		return agentConstructor;
	}



	/**
	 * Randomize the placement of individuals in a subpopulation
	 * 
	 * @param state
	 * @param sp
	 */
	protected static void randomizeSubpopulation(EvolutionState state, Subpopulation sp) {
		int subpop_size = sp.individuals.length;
		for (int j = 0; j < subpop_size; j++) {
			Individual temp = sp.individuals[j];
			int random_ndx = state.random[0].nextInt(subpop_size);
			sp.individuals[j] = sp.individuals[random_ndx];
			sp.individuals[random_ndx] = temp;
		}
	}



	/**
	 * Execute the individual models as individual threads; threads can timeout
	 * and cause the program to halt.
	 * 
	 * @param state
	 * @param models
	 */
	protected static void executeModels(EvolutionState state, ECSimpleMarketSimulation[] models) {
		Parameter base = new Parameter("agency");
		int num_threads = state.parameters.getIntWithDefault(base.push("threads"), null, 1);
		int thread_timeout = state.parameters.getIntWithDefault(base.push("thread_timeout"), null, 20);
		System.err.println(">>> NumThreads: " + num_threads + " threads\t\t" + thread_timeout + " minutes.");
		ExecutorService thread_pool = Executors.newFixedThreadPool(num_threads);
		for (int k = 0; k < models.length; k++) {
			thread_pool.execute(models[k]);
		}
		thread_pool.shutdown();
		try {
			thread_pool.awaitTermination(thread_timeout, TimeUnit.MINUTES);
		} catch (Exception e) {
			System.err.println("There was a problem executing simulation threads.");
			e.printStackTrace();
			System.exit(1);
		}
	}



	@Override
	public boolean runComplete(EvolutionState state) {
		// TODO Auto-generated method stub
		return false;
	}

}
