package abce.ecj;


import java.lang.reflect.*;

import abce.agency.ec.*;
import abce.agency.ec.ecj.*;
import abce.agency.firm.*;
import ec.*;
import ec.gp.*;
import ec.simple.*;
import ec.util.*;
import evoict.reflection.*;



public class OligopolyProblem extends Problem implements CallableGroupProblemForm {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	// The name of the domain problem's parameter base
	public String				domain_name;

	// The file path of the domain configuration file
	public String				domain_config;

	public static final String	P_NAME				= "name";
	public static final String	P_CONFIG			= "config";

	// Instance-specific members; these should be reset on clone
	protected EvolutionState	state;
	protected EvaluationGroup	group;
	protected int				threadnum;



	@Override
	public void setup(final EvolutionState state, Parameter base) {
		super.setup(state, base);
		Parameter defbase = defaultBase();
		domain_name = state.parameters.getString(base.push(P_NAME), defbase.push(P_NAME));
		domain_config = state.parameters.getString((new Parameter(domain_name)).push(P_CONFIG), defbase.push(P_CONFIG));
	}



	/**
	 * Reset all non-prototype fields.
	 */
	@Override
	public void reset() {
		state = null;
		group = null;
		threadnum = -1;
	}



	@Override
	/**
	 * Because information about Stimulus method paths are not available until GPTrees are 
	 * first evaluated, there is the real possibility of a race condition unless the 
	 * evaluator uses an agent only once per generation.  The big problem with this is it 
	 * makes things like multiple evaluation and independent evaluation impossible without 
	 * creating the race condition.  Consequently, the only way that we can guarantee that 
	 * an individual is identical to all eval threads is to make sure that every GPTree has been 
	 * evaluated with their respective MethodDictionaries once before problems are evaluated. 
	 *  This is certainly not as efficient as leaving the possibility of trees being 
	 *  first evaluated during domain model execution, but I don't see a good way around 
	 *  it.
	 *  
	 *  This method should only be called once by the Evaluator prior to problem evaluation.
	 * 
	 */
	public void preprocessPopulation(EvolutionState state, int threadnum) {
		for (int sp = 0; sp < state.population.subpops.length; sp++) {
			Subpopulation subpop = state.population.subpops[sp];
			try {

				Class<? extends StimulusResponse>[] sr_cls = getSRClasses(state, sp);
				StimulusResponse[] srs = new StimulusResponse[sr_cls.length];

				for (int srndx = 0; srndx < srs.length; srndx++) {
					try {
						srs[srndx] = sr_cls[srndx].newInstance();
					} catch (Exception e) {
						e.printStackTrace();
						state.output
								.fatal("Unable to preprocess oligopoly population because I can't create a new instance of the stimulus response "
										+ sr_cls[srndx]);
					}
				}

				for (int indndx = 0; indndx < state.population.subpops[sp].individuals.length; indndx++) {
					GPIndividual gpind = (GPIndividual) subpop.individuals[indndx];
					if (srs.length != gpind.trees.length) {
						state.output.fatal("Expected the same number of trees as stimulus responses.  Abort.");
					}
					for (int trndx = 0; trndx < gpind.trees.length; trndx++) {
						if (srs[trndx] == null)
							continue;
						initStimulusResponses(state, threadnum, gpind.trees[trndx].child, srs[trndx]);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				state.output
						.fatal("Unable to preprocess oligopoly population because I can't retrieve the stimulus response classes for the species of supopulation "
								+ sp);
			}

		}
	}



	/**
	 * Private assistant method to recursively initialize all Stimuliable
	 * GPNodes.
	 * 
	 * @param state
	 * @param threadnum
	 * @param node
	 * @param sr
	 */
	private static void initStimulusResponses(EvolutionState state, int threadnum, GPNode node, StimulusResponse sr) {
		if (node instanceof SRStimulable) {
			((SRStimulable) node).setStimulusPath(state, threadnum, (RestrictedMethodDictionary) sr.dictionary());
		}
		if (node instanceof SRResponsive) {
			((SRResponsive) node).setResponse(state, sr);
		}
		if (node.children != null)
			for (GPNode child : node.children)
				initStimulusResponses(state, threadnum, child, sr);
	}



	@Override
	public void postprocessPopulation(EvolutionState state, int threadnum) {
		return;
	}



	@Override
	/**
	 * Evaluate the problem.
	 * 
	 * @param base_state
	 * 		Should be able to be casted to EPSimpleEvolutionState
	 * @param ind
	 * 		A list of individuals to include in the model
	 * @param updateFitness
	 * 		Whether or not the fitnesses should be updated for each individual
	 * @param subpops
	 * 		The subpopulations in which the individuals belong
	 * @param threadnum
	 * 		The thread number
	 */
	public void setupForEvaluation(EvolutionState state, EvaluationGroup g, int threadnum) {
		this.state = state;
		this.group = g;
		this.threadnum = threadnum;
	}



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
			Parameter tree_base = species_base.push("tree").push(Integer.toString(tree_ndx));
			String sr_name = state.parameters.getString(tree_base.push("sr"), null);
			@SuppressWarnings("unchecked")
			Class<? extends StimulusResponse> sr_class = (sr_name == null) ? null
					: (Class<? extends StimulusResponse>) Class.forName(sr_name);
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
		// System.err.println("Class to find constructor for: " +
		// agentClass.getCanonicalName());
		agentConstructor = (Constructor<ECJEvolvableAgent>) agentClass.getConstructor((Class<?>[]) null);
		return agentConstructor;
	}

}
