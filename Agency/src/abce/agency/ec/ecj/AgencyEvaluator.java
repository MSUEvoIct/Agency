package abce.agency.ec.ecj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.Evaluator;
import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleFitness;
import ec.util.Parameter;

public class AgencyEvaluator extends Evaluator implements FitnessListener {
	private static final long serialVersionUID = 1L;

	private Parameter parameterRoot = null;

	private Map<Individual, List<Double>> fitnesses = null;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		parameterRoot = base;

	}

	@Override
	public void evaluatePopulation(EvolutionState evoState) {

		// clear out fitnesses, start with a new average every generation
		fitnesses = new HashMap<Individual, List<Double>>();

		// Get the grouper and runner
		GroupCreator groupCreator = AgencyEvaluator.getGroupCreator(evoState);
		AgencyRunner simRunner = AgencyEvaluator.getRunner(evoState);
		
		// Add the population to the grouper
		groupCreator.addPopulation(evoState);
		
		
		/* Have the runner execute the simulations.  They'll call us with
		 * updateFitness when they finish, so when this call returns, we should
		 * have all the measured fitnesses in this.fitnesses.
		*/
		simRunner.runSimulations(groupCreator, this);

		// Update Individual fitness with average.
		for (Individual ind : fitnesses.keySet()) {
			double fitnessSum = 0.00;
			List<Double> fitnessList = fitnesses.get(ind);
			for (Double d : fitnessList) {
				fitnessSum += d;
			}
			Double fitnessAverage = fitnessSum / fitnessList.size();

			SimpleFitness sf = (SimpleFitness) ind.fitness;
			sf.setFitness(evoState, fitnessAverage.floatValue(), false);
		}

	}

	@Override
	public synchronized void updateFitness(Individual ind, Double fit) {
		List<Double> indFitnessSamples = fitnesses.get(ind);
		if (indFitnessSamples == null) {
			indFitnessSamples = new ArrayList<Double>();
			fitnesses.put(ind, indFitnessSamples);
		}
		indFitnessSamples.add(fit);

	}

	@Override
	public boolean runComplete(EvolutionState evoState) {
		return false;
	}

	public static AgencyRunner getRunner(EvolutionState evoState) {
		Parameter arParam = new Parameter("eval.simrunner");

		// Instantiate and initialize the simulation runner
		Class simRunnerClass = (Class) evoState.parameters
				.getClassForParameter(arParam, null, AgencyRunner.class);

		AgencyRunner simRunner = null;

		try {
			simRunner = (AgencyRunner) simRunnerClass.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		simRunner.setup(evoState, arParam);

		return simRunner;
	}

	public static GroupCreator getGroupCreator(EvolutionState evoState) {
		Parameter gcParam = new Parameter("eval.groupcreator");

		// Instantiate and initialize the GroupCreator
		@SuppressWarnings("rawtypes")
		Class groupCreatorClass = (Class) evoState.parameters
				.getClassForParameter(gcParam, null, GroupCreator.class);
		GroupCreator groupCreator = null;

		try {
			groupCreator = (GroupCreator) groupCreatorClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Could not initialize group creator ");
		}
		groupCreator.setup(evoState, gcParam);
		return groupCreator;
	}

	public static AgencyModel getSim(EvolutionState evoState,
			Parameter base) {
		Class groupCreatorClass = (Class) evoState.parameters
				.getClassForParameter(base.push("sim"), null,
						AgencyModel.class);
		AgencyModel sim = null;

		try {
			sim = (AgencyModel) groupCreatorClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException("Could not initialize simulation class");
		} catch (IllegalAccessException e) {
			throw new RuntimeException(
					"Illegal access trying to initialize simulation class");
		}

		sim.setSeed(evoState.random[0].nextInt());
		sim.setup(evoState, base.push("sim"));

		return sim;
	}

}
