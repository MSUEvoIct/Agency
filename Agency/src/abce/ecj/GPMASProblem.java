package abce.ecj;


import java.lang.reflect.Constructor;

import abce.agency.ec.StimulusResponse;
import abce.agency.ec.ecj.ECJEvolvableAgent;
import abce.agency.ec.ecj.SRResponsive;
import abce.agency.ec.ecj.SRStimulable;
import abce.agency.reflection.RestrictedMethodDictionary;
import ec.EvolutionState;
import ec.Subpopulation;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;



public abstract class GPMASProblem extends MASProblem {

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
