package ec.agency;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;


import ec.EvolutionState;
import ec.Individual;
import ec.agency.eval.AgencyEvaluator;
import ec.agency.eval.AgencyRunner;
import ec.agency.eval.FitnessListener;
import ec.agency.eval.GroupCreator;
import ec.agency.io.DelimitedOutFile;
import ec.agency.io.FileManager;
import ec.util.Parameter;

/**
 * In evolutionary environments where the measurement of fitness of an
 * individual depends on the population characteristics, it is not possible to
 * directly compare inter-generational fitness by using average fitness scores
 * calculated from a single generation.
 * 
 * @author kkoning
 * 
 */
public class IntergenerationalStatistics extends ec.Statistics implements
		FitnessListener {
	private static final long serialVersionUID = 1L;

	private static final String fnPrefix = "igCkp";

	/*
	 * TODO: Matt, fixme!  :)
	 */
	public static FileManager fm = new FileManager();
	static {
		fm = new FileManager();
		try {
			fm.initialize(".");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public final String productionFormat = "RefGen%d,Lag%d,Subpopulation%d,Fitness%f";
	public final String productionFile = "ixgStats.csv.gz";
	
	
	/**
	 * 
	 */
	private String popCheckpointPrefix;
	private List<Integer> lagComparisons = new ArrayList<Integer>();
	private int modulo = 1;

	private Map<Individual, List<Double>> fitnesses;
	private Map<Individual, Integer> generation;
	private Map<Individual, Integer> subpopulation;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		// Figure out the where to store the checkpoints
		popCheckpointPrefix = state.parameters.getString(
				base.push("popCheckpointPrefix"), null);
		if (popCheckpointPrefix == null) {
			System.err
					.println("Must specify checkpoint prefix (e.g. directory) for IntergenerationStatistics");
			System.exit(-1);
		}

		// During which generations should we run these statistics?
		modulo = state.parameters.getIntWithDefault(base.push("modulo"), null,
				1);

		// Which generations should we compare them with?
		int numLags = state.parameters.getInt(base.push("num-lags"), null);
		if (numLags <= 0)
			throw new RuntimeException(
					"IntergenerationalStatistics needs at least one lag period");

		for (int i = 0; i < numLags; i++) {
			int lag = state.parameters.getInt(base.push("lag." + i), null);
			if (lag <= 0)
				throw new RuntimeException(
						"IntergenerationalStatistics lags are specified in positive integers");
			lagComparisons.add(lag);
		}

	}

	/*
	 * The most important thing is that we don't split checkpointing and
	 * intergenerational evaluation across the breeding boundary, because that
	 * would result in competing the same population against itself.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see ec.Statistics#postEvaluationStatistics(ec.EvolutionState)
	 */
	@Override
	public void postEvaluationStatistics(EvolutionState evoState) {
		super.postEvaluationStatistics(evoState);

		
		// Serialize this evoState for the next generation
		serializeCheckpoint(evoState);

		/*
		 * Set up the Maps for tracking information on individuals. These must
		 * be identitymaps so that we're tracking actual Java objects and not
		 * genome values.
		 */
		fitnesses = new IdentityHashMap<Individual,List<Double>>();
		generation = new IdentityHashMap<Individual,Integer>();
		subpopulation = new IdentityHashMap<Individual,Integer>();
		
		/*
		 * Do an inter-generational comparison between the current generation G
		 * and each lagged generation G-n, where n is a value in
		 * this.lagComparisons, but only in those generations indicated by the
		 * modulo value
		 */
		if (evoState.generation % modulo == 0) {
			for (int lagGeneration : lagComparisons) {
				if (lagGeneration < evoState.generation + 1) {
					System.out.println("Doing intergenerational statistics b/t gen " + evoState.generation + 
							" and " + (evoState.generation - lagGeneration));
					compare(evoState, lagGeneration);
					System.out.println("Finished intergenerational statistics b/t gen " + evoState.generation + 
							" and " + (evoState.generation - lagGeneration));
					
				}
			}
		}

		/*
		 * Output the data on the comparisons.
		 */
		// "RefGen%d,Lag%d,Subpopulation%d,Fitness%f";
		DelimitedOutFile out;
		try {
			out = fm.getDelimitedOutFile(productionFile, productionFormat);
		} catch (IOException e) {
			throw new RuntimeException("Cannot open ixg comparison data output file");
		}
		for (Individual ind : fitnesses.keySet()) {
			double avgFit = avgFitness(fitnesses.get(ind));
			out.write(evoState.generation,  // RefGen
					evoState.generation - generation.get(ind),  // Lag
					subpopulation.get(ind),  // Subpopulation
					avgFit    // avg fitness (of individual)
					);
			
			
		}
		
		
		
		
		
		
		/*
		 * Delete checkpoints that are older than what we need to perform the
		 * largest lag comparison. Saving a checkpoing from _EVERY_ generation
		 * would consume a lot of disk space; resuming and repeating
		 * evolutionary runs is what the normal checkpointing facility is for.
		 */
		cleanupCheckpoints(evoState.generation);

		/*
		 * Reset state information regarding the population comparisons to
		 * ensure that state does not leak from one generation to the other.
		 */
		fitnesses = null;
		generation = null;
		subpopulation = null;

	}

	private void compare(EvolutionState evoState, int lagGeneration) {
		// TODO Auto-generated method stub
		
		/*
		 * Get the two subpopulations we're comparing, track them, then add them
		 * a new instance of the GroupCreator.
		 */
		
		// De-serialize the lagged generation
		EvolutionState laggedState = deserializeCheckpoint(evoState.generation - lagGeneration);
		
		// Track information from both EvolutionStates
		trackIndividuals(evoState);
		trackIndividuals(laggedState);
		
		// Get the GroupCreator
		GroupCreator gc = AgencyEvaluator.getGroupCreator(evoState);
		
		// Add the populations to the GroupCreator
		gc.addPopulation(evoState);
		gc.addPopulation(laggedState);
		
		// Get the AgencyRunner
		AgencyRunner ar = AgencyEvaluator.getRunner(evoState);
		
		// Have the runner execute the simulations; use us as the fitness listener
		ar.runSimulations(gc, this);
		
		
		// All the fitnesses and other information should now be in the associated variables.
		
		

	}

	/**
	 * We need to keep track of which individuals are associated with which
	 * generation and which subpopulation.
	 * 
	 * @param evoState
	 */
	private void trackIndividuals(EvolutionState evoState) {
		for (int i = 0; i < evoState.population.subpops.length; i++) {
			for (int j = 0; j < evoState.population.subpops[i].individuals.length; j++) {
				Individual ind = evoState.population.subpops[i].individuals[j];
				generation.put(ind, evoState.generation);
				subpopulation.put(ind, i);
			}
		}

	}

	private static double avgFitness(List<Double> samples) {
		double sum = 0.0;
		for (Double sample : samples)
			sum += sample;
		
		return sum / samples.size();
	}
	
	
	/**
	 * After all lag comparisons are done, delete checkpoints that are older
	 * than the maximum age necessary for the longest lag comparison.
	 * 
	 * @param gen
	 *            The current generation
	 */
	protected void cleanupCheckpoints(int gen) {

		// Get the longest value we have for lag
		int longestLag = 1;
		for (int lag : lagComparisons)
			if (lag > longestLag)
				longestLag = lag;

		
		/*
		 * Delete the checkpoint for the generation of the longest lag. This
		 * function is called in postEvaluationStatistics, _after_ the
		 * evaluation of this generation, and the generation is incremented
		 * before postEvaluationStatistics will be called again.
		 */
		String fileName = getFileName(gen - longestLag);
		File fileToDelete = new File(fileName);
		fileToDelete.delete();
	}

	protected EvolutionState deserializeCheckpoint(int gen) {
		String fileName = getFileName(gen);
		File fileToDeserialize = new File(fileName);

		EvolutionState deserialzedEvoState = null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(fileToDeserialize);
			ois = new ObjectInputStream(fis);
			deserialzedEvoState = (EvolutionState) ois.readObject();

		} catch (FileNotFoundException e) {
			throw new RuntimeException(
					"Could not deserialize ixg checkpoint file " + fileName);
		} catch (IOException e) {
			throw new RuntimeException("IO error in deserialization");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Type error in deserialization");
		} finally {
			try {
				ois.close();
				fis.close();
			} catch (Exception e) {
				throw new RuntimeException(
						"Got exception trying to close checkpoint file?!");
			}
		}

		return deserialzedEvoState;
	}

	@Override
	public void finalStatistics(EvolutionState state, int result) {
		/*
		 * TODO: Clean up all the stale checkpoints this leaves around (we've
		 * only deleted those that are older than our oldest lag, so we still
		 * have the newer ones to delete after the entire simulation run is
		 * finished).
		 */
		super.finalStatistics(state, result);
	}

	protected void serializeCheckpoint(EvolutionState evoState) {

		FileOutputStream fs = null;
		ObjectOutputStream oos = null;
		String fileName = getFileName(evoState.generation);
		try {
			fs = new FileOutputStream(new File(fileName));
			oos = new ObjectOutputStream(fs);
			oos.writeObject(evoState);

		} catch (FileNotFoundException e) {
			System.err
					.println("IntergenerationalStatistics could not open checkpoint file '"
							+ fileName + "' for writing");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Could not write to checkpoint file '"
					+ fileName + "'");
			e.printStackTrace();
		} finally {
			try {
				oos.close();
				fs.close();
			} catch (IOException e) {
				// Severe error, bail!
				System.err.println("Could even close file '" + fileName + "'");
				e.printStackTrace();
				System.exit(-1);
			}
		}

	}

	/**
	 * @param gen the generation sequence number
	 * @return the name of the file, including path from the simulation's working
	 * directory, corresponding to the temporary checkpoint for that generation.
	 */
	private final String getFileName(int gen) {
		return popCheckpointPrefix + "/" + fnPrefix + "." + gen
				+ ".evoState.ser";
	}

	@Override
	public synchronized void updateFitness(Individual ind, Double fit) {
		List<Double> fitList = fitnesses.get(ind);
		if (fitList == null) {
			fitList = new ArrayList<Double>();
			fitnesses.put(ind, fitList);
		}
		fitList.add(fit);
	}

}
