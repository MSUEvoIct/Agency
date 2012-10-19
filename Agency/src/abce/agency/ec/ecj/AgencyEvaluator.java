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
		fitnesses =  new HashMap<Individual, List<Double>>();
		
		
		// Determine which individuals will be grouped together
		// for execution

		// Instantiate and initialize the GroupCreator
		@SuppressWarnings("rawtypes")
		Class groupCreatorClass = (Class) evoState.parameters
				.getClassForParameter(parameterRoot.push("groupcreator"), null,
						GroupCreator.class);
		GroupCreator groupCreator = null;

		try {
			groupCreator = (GroupCreator) groupCreatorClass.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		groupCreator.setup(evoState, parameterRoot.push("groupcreator"));

		// Instantiate and initialize the simulation runner
		Class simRunnerClass = (Class) evoState.parameters
				.getClassForParameter(parameterRoot.push("simrunner"), null,
						AgencyRunner.class);

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
		simRunner.setup(evoState, parameterRoot.push("simrunner"));

		
		simRunner.runSimulations(groupCreator, this);

		
		// Update Individual fitness with average.
		for (Individual ind : fitnesses.keySet()) {
			double fitnessSum = 0.00;
			List<Double> fitnessList = fitnesses.get(ind);
			for (Double d : fitnessList) {
				fitnessSum += d;
			}
			Double fitnessAverage = fitnessSum / fitnessList.size();
			
//			if (true) {
//				System.out.println(ind + " fitnesses -> " + fitnessList);
//			}
			
			SimpleFitness sf = (SimpleFitness) ind.fitness;
			sf.setFitness(evoState, fitnessAverage.floatValue(), false);
		}
		
		
	}

	@Override
	public synchronized void updateFitness(Individual ind, Double fit) {
		List<Double> indFitnessSamples = fitnesses.get(ind);
		if (indFitnessSamples == null) {
			indFitnessSamples = new ArrayList<Double>();
			fitnesses.put(ind,indFitnessSamples);
		}
		indFitnessSamples.add(fit);
		
	}

	@Override
	public boolean runComplete(EvolutionState evoState) {
		// ideal individuals cannot be automatically detected
		return false;
	}
	
	public static AgencyECJSimulation getSim(EvolutionState evoState, Parameter base) {
		Class groupCreatorClass = (Class) evoState.parameters
				.getClassForParameter(base.push("sim"), null, AgencyECJSimulation.class);
		AgencyECJSimulation sim = null;

		try {
			sim = (AgencyECJSimulation) groupCreatorClass.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sim.setSeed(evoState.random[0].nextInt());
		sim.setup(evoState, base.push("sim"));

		return sim;
	}
	
	

}
