package abce.ecj;


import java.lang.reflect.*;

import abce.agency.ec.*;
import abce.agency.ec.ecj.*;
import abce.agency.firm.*;
import ec.*;
import ec.simple.*;



public class OligopolyProblem extends GPMASProblem implements CallableGroupProblemForm {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;



	/**
	 * Actually instantiate and run the domain model.
	 */
	@Override
	public Object call() throws Exception {
		EPSimpleEvolutionState epstate = (EPSimpleEvolutionState) state;

		// Create the domain model simulation
		int num_ind = group.ind.length;
		ECJEvolvableAgent[] agents = new ECJEvolvableAgent[num_ind];
		int seed = epstate.random[threadnum].nextInt();

		OligopolySimulation model = new OligopolySimulation(seed, domain_config, epstate.generation);
		populateModel(epstate, threadnum, group.ind, group.subpops, model, agents);

		// Run the model
		Integer status = model.call();

		// Set the fitness

		for (int k = 0; k < num_ind; k++) {
			if (group.evaluate_fitness[k]) {
				AgencyGPIndividual i = ((AgencyGPIndividual) group.ind[k]);
				SimpleFitness f = ((SimpleFitness) i.fitness);
				f.setFitness(state, (float) agents[k].getFitness(), false);
				i.evaluated = true;
			}

		}
		return status;
	}



	/**
	 * Populate the models with agents bound to GPIndividuals, associated with
	 * StimulusResponse objects
	 * 
	 * @param state
	 *            The evolution state making the request
	 * @param thread
	 *            The thread number of the evolution state
	 * @param inds
	 *            The individuals to be placed in the domain model
	 * @param subpops
	 *            The subpopulations from which the individuals come
	 * @param model
	 *            The model being populated
	 * @param agents
	 *            The agents created for the model (1:1 with Individuals)
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	protected static void populateModel(EvolutionState state, int thread, Individual[] inds, int[] subpops,
			OligopolySimulation model, ECJEvolvableAgent[] agents)
			throws SecurityException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
			IllegalArgumentException, InstantiationException, IllegalAccessException {
		int num_ind = inds.length;
		for (int ndx = 0; ndx < num_ind; ndx++) {
			Constructor<ECJEvolvableAgent> agent_constructor = findAgentConstructor(state, subpops[ndx]);
			Class<? extends StimulusResponse>[] sr_classes = getSRClasses(state, subpops[ndx]);
			ECJEvolvableAgent agent = setupAgent(agent_constructor, model);
			bindSR(state, thread, subpops[ndx], inds[ndx], agent, sr_classes);
			agents[ndx] = agent;
		}
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
			OligopolySimulation model)
			throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		ECJEvolvableAgent agent = constructor.newInstance();
		if (agent instanceof Firm) {
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
	protected static void bindSR(EvolutionState state, int thread, int subpop_ndx, Individual i,
			ECJEvolvableAgent agent,
			Class<? extends StimulusResponse>[] sr_classes) {
		AgencyGPIndividual ind = (AgencyGPIndividual) i;
		agent.register(state, thread, ind, sr_classes);
	}

}
