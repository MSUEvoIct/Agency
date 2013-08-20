package ec.agency.eval;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ec.Evaluator;
import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.util.Parameter;

public class AgencyEvaluator extends Evaluator {
	private static final long serialVersionUID = 1L;

	// Parameters for components
	Parameter pRoot = new Parameter("eval");
	Parameter pModel = pRoot.push("model");
	Parameter pGroupCreator = pRoot.push("groupcreator");
	Parameter pRunner = pRoot.push("runner");
	Parameter pFitnessAggregator = pRoot.push("fitnessaggregator");
	Parameter pProgress = pRoot.push("progress");

	// The types of those components
	Class<? extends AgencyModel> modelClass;
	Class<? extends GroupCreator> groupCreatorClass;
	Class<? extends AgencyRunner> runnerClass;
	Class<? extends FitnessAggregator> fitnessAggregatorClass;

	// We'll keep a single runner for the whole evolutionary run
	AgencyRunner runner;

	// give unique (per-generation) ids to simlations.
	int simulationID;
	int numModelsRun;
	
	
	// output progress?
	boolean progress = false;

	private Map<Individual, List<Double>> fitnesses = null;

	@SuppressWarnings("unchecked")
	@Override
	public void setup(EvolutionState evoState, Parameter base) {

		// get java types for components from ecj configuration file
		modelClass = (Class<? extends AgencyModel>) evoState.parameters
				.getClassForParameter(pModel, null, AgencyModel.class);
		groupCreatorClass = (Class<? extends GroupCreator>) evoState.parameters
				.getClassForParameter(pGroupCreator, null, GroupCreator.class);
		runnerClass = (Class<? extends AgencyRunner>) evoState.parameters
				.getClassForParameter(pRunner, null, AgencyRunner.class);
		fitnessAggregatorClass = (Class<? extends FitnessAggregator>) evoState.parameters
				.getClassForParameter(pFitnessAggregator, null,
						FitnessAggregator.class);
		progress = evoState.parameters.getBoolean(pProgress, null, false);
		

		// Initialize a runner to use for the duration
		try {
			runner = runnerClass.newInstance();
			runner.setup(evoState, pRunner);
		} catch (Exception e) {
			String msg = "Could not initialize/setup AgencyRunner "
					+ runnerClass.getCanonicalName();
			throw new RuntimeException(msg, e);
		}

	}

	@Override
	public void evaluatePopulation(EvolutionState evoState) {

		// Get start time
		Date start = new Date();

		int simulationID = 0;
		numModelsRun = 0;

		// Get the grouper and populate it
		GroupCreator groupCreator = getGroupCreator(evoState);
		groupCreator.addPopulation(evoState);

		// Initialize a new fitness aggregator
		FitnessAggregator fa = getFitnessAggregator(evoState,
				pFitnessAggregator);

		// Run all the models
		for (EvaluationGroup evalGroup : groupCreator) {
			ModelRunnerHelper mrh = new ModelRunnerHelper();
			mrh.evoState = evoState;
			mrh.simulationID = simulationID++;
			mrh.seed = evoState.random[0].nextInt();
			mrh.eg = evalGroup;
			mrh.fa = fa;

			runner.runModel(mrh);
		}

		// Wait for any/all asynchronously scheduled models to finish
		runner.finish();

		long msElapsed = new Date().getTime() - start.getTime();

		double simPerSecond = (simulationID * 1000) / (msElapsed + 1);
		DecimalFormat df = new DecimalFormat("#,##0.##");

		evoState.output.println(numModelsRun + "/" + simulationID + " models executed in "
				+ msElapsed + "ms, " + df.format(simPerSecond) + "/sec", 1);

		// Tell the aggregator to update the population
		fa.updatePopulation(evoState);

	}

	public AgencyRunner getRunner(EvolutionState evoState) {
		AgencyRunner runner = null;

		try {
			runner = (AgencyRunner) runnerClass.newInstance();
			runner.setup(evoState, pRunner);
		} catch (Exception e) {
			String msg = "Could not initialize/setup AgencyRunner "
					+ runnerClass.getCanonicalName();
			throw new RuntimeException(msg, e);
		}

		return runner;
	}

	public GroupCreator getGroupCreator(EvolutionState evoState) {
		GroupCreator groupCreator = null;

		try {
			groupCreator = (GroupCreator) groupCreatorClass.newInstance();
			groupCreator.setup(evoState, pGroupCreator);
		} catch (Exception e) {
			String msg = "Could not initialize/setup GroupCreator "
					+ groupCreatorClass.getCanonicalName();
			throw new RuntimeException(msg, e);
		}
		return groupCreator;
	}

	public AgencyModel getModel(EvolutionState evoState, Parameter base,
			int seed) {
		AgencyModel model = null;

		try {
			model = (AgencyModel) modelClass.newInstance();
			model.setup(evoState, pModel);
			model.setSeed(seed);
		} catch (Exception e) {
			String msg = "Count not initialize/setup AgencyModel "
					+ modelClass.getCanonicalName();
			throw new RuntimeException(msg, e);
		}
		return model;
	}

	public FitnessAggregator getFitnessAggregator(EvolutionState evoState,
			Parameter base) {
		FitnessAggregator toReturn = null;

		try {
			toReturn = (FitnessAggregator) fitnessAggregatorClass.newInstance();
			toReturn.setup(evoState, pFitnessAggregator);
		} catch (Exception e) {
			String msg = "Count not initialize/setup FitnessAgregator "
					+ fitnessAggregatorClass.getCanonicalName();
			throw new RuntimeException(msg, e);
		}
		return toReturn;
	}

	@Override
	public boolean runComplete(EvolutionState evoState) {
		// We can't find ideal individuals; if there is a strategic element
		// to the game, fitness depends on the population context.
		return false;
	}

	public class ModelRunnerHelper implements Runnable {

		FitnessAggregator fa;
		EvolutionState evoState;
		int seed;
		int simulationID;

		EvaluationGroup eg;

		@Override
		public void run() {
			try {
				AgencyModel model = getModel(evoState, null, seed);
				model.setSeed(seed);
				model.setGeneration(evoState.generation);
				model.setSimulationID(simulationID);
				model.setEvaluationGroup(eg);

				model.run();

				// Output progress to console
				if (progress)
					System.out.print(simulationID + "\r"); 

				Map<Individual, Fitness> fitnessSamples = model.getFitnesses();

				if (fitnessSamples == null)
					throw new RuntimeException(
							"Model returned null fitness samples");

				if (fitnessSamples.size() == 0)
					throw new RuntimeException(
							"Model returned empty fitness samples");

				for (Map.Entry<Individual, Fitness> entry : fitnessSamples
						.entrySet()) {
					fa.addSample(entry.getKey(), entry.getValue());
				}
			} catch (Exception e) {
				System.out.println("Error running simulation");
				e.printStackTrace(System.err);
				System.exit(-1);
			}
			incModelsRun();

		}

	}

	private synchronized void incModelsRun() {
		numModelsRun++;
	}
	
	@Override
	public void closeContacts(EvolutionState state, int result) {
		// We don't use this
	}

	@Override
	public void initializeContacts(EvolutionState state) {
		// We don't use this
	}

	@Override
	public void reinitializeContacts(EvolutionState state) {
		// We don't use this
	}

}
