package abce.io.simple.ecj;


import java.io.*;
import java.lang.reflect.*;

import abce.agency.ec.*;
import abce.agency.ec.ecj.*;
import abce.agency.engine.*;
import abce.agency.firm.*;
import abce.io.simple.*;
import ec.*;
import ec.gp.*;
import ec.util.*;



public class SimpleFirmFactoryEvaluator extends Evaluator {

	private static final long	serialVersionUID	= 1L;
	boolean						inStep				= false;



	@Override
	public void evaluatePopulation(EvolutionState state) {
		if (inStep)
			throw new RuntimeException("evaluatePopulaiton() is not reentrant, yet is being called recursively.");

		inStep = true;

		Parameter base = new Parameter("agency");
		int numThreads = state.parameters.getIntWithDefault(base.push("threads"), null, 1);
		int numChunks = state.parameters.getInt(base.push("chunks"), null);
		int numSteps = state.parameters.getInt(base.push("steps"), null);
		boolean simternetCheckpoint = state.parameters.getBoolean(base.push("checkpoint"), null, false);
		int simternetCheckpointModulo = state.parameters.getInt(base.push("checkpoint-modulo"), new Parameter(
				"checkpoint-modulo"));
		String config_path = state.parameters.getString(base.push("config"), null);

		SimpleAgencyConfig config = null;
		try {
			config = new SimpleAgencyConfig(config_path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		ECSimpleMarketSimulation[] models = new ECSimpleMarketSimulation[numChunks];
		for (int i = 0; i < numChunks; i++) {
			int seed = state.random[0].nextInt();
			models[i] = new ECSimpleMarketSimulation(seed, (SimpleAgencyConfig) config.clone(), i, state.generation);
		}

		try {
			populateModels(state, models);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		// TODO: Write execute models method
		executeModels(state, models);

		// TODO: Write evaluate fitness method
		// evaluateFitness(state);

		inStep = false;
	}



	protected void populateModels(EvolutionState state, ECSimpleMarketSimulation[] models)
			throws SecurityException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
			IllegalArgumentException, InstantiationException, IllegalAccessException {
		for (int sp_ndx = 0; sp_ndx < state.population.subpops.length; sp_ndx++) {
			Subpopulation sp = state.population.subpops[sp_ndx];
			int subpop_size = sp.individuals.length;
			Constructor<ECJEvolvableAgent> agent_constructor = findAgentConsrructor(state, sp_ndx);
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



	protected static Class<? extends StimulusResponse>[] getSRClasses(EvolutionState state, int sp_ndx)
			throws ClassNotFoundException {
		Parameter species_base = new Parameter("pop").push("subpop").push(Integer.toString(sp_ndx)).push("species");
		int num_trees = state.parameters.getInt(species_base.push("numtrees"), null);
		@SuppressWarnings("unchecked")
		Class<? extends StimulusResponse>[] sr_classes = (Class<? extends StimulusResponse>[]) new Class<?>[num_trees];
		for (int tree_ndx = 0; tree_ndx < num_trees; tree_ndx++) {
			Parameter tree_base = species_base.push(Integer.toString(sp_ndx));
			String sr_name = state.parameters.getString(tree_base.push("sr"), null);
			@SuppressWarnings("unchecked")
			Class<? extends StimulusResponse> sr_class = (Class<? extends StimulusResponse>) Class.forName(sr_name);
			sr_classes[tree_ndx] = sr_class;
		}
		return sr_classes;

	}



	protected static ECJEvolvableAgent setupAgent(Constructor<ECJEvolvableAgent> constructor,
			ECSimpleMarketSimulation model)
			throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		ECJEvolvableAgent agent = constructor.newInstance(model);
		if (agent instanceof ECJFirm) {
			model.setupFirm((Firm) agent);
		}
		return agent;
	}



	protected static void bindSR(EvolutionState state, int subpop_ndx, int ind_ndx, ECJEvolvableAgent agent,
			Class<? extends StimulusResponse>[] sr_classes) {
		GPIndividual ind = (GPIndividual) state.population.subpops[subpop_ndx].individuals[ind_ndx];
		agent.register(ind, sr_classes.clone());
	}



	@SuppressWarnings("unchecked")
	protected static Constructor<ECJEvolvableAgent> findAgentConsrructor(EvolutionState state, int subpop_number)
			throws ClassNotFoundException, SecurityException, NoSuchMethodException {
		// and set up the ability to create those agents.
		String agentClassName = null;
		Class<?> agentClass = null;
		Constructor<ECJEvolvableAgent> agentConstructor = null;
		Parameter p = new Parameter("pop").push("subpop").push(Integer.toString(subpop_number)).push("species")
				.push("agent");
		agentClassName = state.parameters.getString(p, null);
		agentClass = Class.forName(agentClassName);
		agentConstructor = (Constructor<ECJEvolvableAgent>) agentClass.getConstructor(MarketSimulation.class);
		return agentConstructor;
	}



	protected static void randomizeSubpopulation(EvolutionState state, Subpopulation sp) {
		int subpop_size = sp.individuals.length;
		for (int j = 0; j < subpop_size; j++) {
			Individual temp = sp.individuals[j];
			int random_ndx = state.random[0].nextInt(subpop_size);
			sp.individuals[j] = sp.individuals[random_ndx];
			sp.individuals[random_ndx] = temp;
		}
	}



	protected static void executeModels(EvolutionState state, ECSimpleMarketSimulation[] models) {

	}



	@Override
	public boolean runComplete(EvolutionState state) {
		// TODO Auto-generated method stub
		return false;
	}

}
