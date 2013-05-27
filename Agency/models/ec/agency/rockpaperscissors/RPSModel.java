package ec.agency.rockpaperscissors;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.agency.eval.AgencyModel;
import ec.agency.eval.EvaluationGroup;
import ec.agency.io.DataOutputFile;
import ec.agency.io.GenerationAggregatingDataOutputFile;
import ec.simple.SimpleFitness;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

public class RPSModel implements AgencyModel {
	private static final long serialVersionUID = 1L;

	// Data output facility
	static final Lock dataOpenLock = new ReentrantLock();
	static DataOutputFile outFile;

	// Game position information
	int generation;
	int simulationID;
	MersenneTwisterFast random;

	int numSteps;
	int step;

	byte[][] history;
	RPSPlayer[] player = new RPSPlayer[2];

	// Payoffs
	public static final byte ROCK = 0;
	public static final byte PAPER = 1;
	public static final byte SCISSORS = 2;

	final float[][] payoffTable = new float[3][3];

	final int[] frequency = new int[3];
	final int[][] frequencyTable = new int[3][3];

	final float[] payoffs = new float[2];

	private void initOutput() {
		dataOpenLock.lock();
		if (outFile == null) {
			String[] header = new String[13];
			header[0] = "Generation";
			header[1] = "SimulationID";
			header[2] = "TimesRock";
			header[3] = "TimesPaper";
			header[4] = "TimesScissors";
			header[5] = "TimesRockRock";
			header[6] = "TimesRockPaper";
			header[7] = "TimesRockScissors";
			header[8] = "TimesPaperPaper";
			header[9] = "TimesPaperScissors";
			header[10] = "TimesScissorsScissors";
			header[11] = "TotalPayoff";
			header[12] = "PayoffDifference";

			outFile = new GenerationAggregatingDataOutputFile("RPSData.tsv",
					header);

		}
		dataOpenLock.unlock();
	}

	@Override
	public void setup(EvolutionState state, Parameter base) {
		ParameterDatabase pd = state.parameters;

		payoffTable[ROCK][ROCK] = pd.getFloat(base.push("payoffs.rock.rock"),
				null);
		payoffTable[ROCK][PAPER] = pd.getFloat(base.push("payoffs.rock.paper"),
				null);
		payoffTable[ROCK][SCISSORS] = pd.getFloat(
				base.push("payoffs.rock.scissors"), null);
		payoffTable[PAPER][ROCK] = pd.getFloat(base.push("payoffs.paper.rock"),
				null);
		payoffTable[PAPER][PAPER] = pd.getFloat(
				base.push("payoffs.paper.paper"), null);
		payoffTable[PAPER][SCISSORS] = pd.getFloat(
				base.push("payoffs.paper.scissors"), null);
		payoffTable[SCISSORS][ROCK] = pd.getFloat(
				base.push("payoffs.scissors.rock"), null);
		payoffTable[SCISSORS][PAPER] = pd.getFloat(
				base.push("payoffs.scissors.paper"), null);
		payoffTable[SCISSORS][SCISSORS] = pd.getFloat(
				base.push("payoffs.scissors.scissors"), null);

		numSteps = pd.getInt(base.push("steps"), null);

		history = new byte[2][numSteps];
		initOutput();

	}

	@Override
	public void setEvaluationGroup(EvaluationGroup evalGroup) {
		// Must contain exactly 2 RPSPlayers

		if (evalGroup.individuals.size() != 2)
			throw new RuntimeException("Must have exactly two players");

		for (int i = 0; i < 2; i++) {
			Individual ind = evalGroup.individuals.get(i);
			if (ind instanceof RPSPlayer) {
				player[i] = (RPSPlayer) ind;
			} else {
				throw new RuntimeException("Competitors must be RPSPlayers");
			}
		}

	}

	@Override
	public void run() {

		// Prepare stimuli; they stay largely the same.
		RPSStimulus[] stimuli = new RPSStimulus[2];
		stimuli[0] = new RPSStimulus();
		stimuli[1] = new RPSStimulus();
		
		stimuli[0].random = random;
		stimuli[1].random = random;

		stimuli[0].myHistory = history[0];
		stimuli[0].oppHistory = history[1];
		stimuli[1].myHistory = history[1];
		stimuli[1].oppHistory = history[0];

		for (int step = 0; step < numSteps; step++) {
			// Update stimuli
			stimuli[0].step = step;
			stimuli[1].step = step;

			byte[] results = new byte[2];
			results[0] = player[0].play(stimuli[0]);
			results[1] = player[1].play(stimuli[1]);

			payoffs[0] += payoffTable[results[0]][results[1]];
			payoffs[1] += payoffTable[results[1]][results[0]];

			history[0][step] = results[0];
			history[1][step] = results[1];

			// Record data for later output
			// simple frequencies
			frequency[results[0]]++;
			frequency[results[1]]++;
			// compound frequencies; combine later
			frequencyTable[results[0]][results[1]]++;

		}

		// Output data & statistics
		// header[0] = "Generation";
		// header[1] = "SimulationID";
		// header[2] = "TimesRock";
		// header[3] = "TimesPaper";
		// header[4] = "TimesScissors";
		// header[5] = "TimesRockRock";
		// header[6] = "TimesRockPaper";
		// header[7] = "TimesRockScissors";
		// header[8] = "TimesPaperPaper";
		// header[9] = "TimesPaperScissors";
		// header[10] = "TimesScissorsScissors";
		// header[11] = "TotalPayoff";
		// header[12] = "PayoffDifference";

		Object[] outData = new Object[13];
		outData[0] = generation;
		outData[1] = simulationID;
		outData[2] = frequency[ROCK];
		outData[3] = frequency[PAPER];
		outData[4] = frequency[SCISSORS];
		outData[5] = frequencyTable[ROCK][ROCK];
		outData[6] = frequencyTable[ROCK][PAPER] + frequencyTable[PAPER][ROCK];
		outData[7] = frequencyTable[ROCK][SCISSORS]
				+ frequencyTable[SCISSORS][ROCK];
		outData[8] = frequencyTable[PAPER][PAPER];
		outData[9] = frequencyTable[PAPER][SCISSORS]
				+ frequencyTable[SCISSORS][PAPER];
		outData[10] = frequencyTable[SCISSORS][SCISSORS];
		outData[11] = payoffs[0] + payoffs[1];
		outData[12] = Math.abs(payoffs[0] - payoffs[1]);

		outFile.writeTuple(outData);

	}

	@Override
	public Map<Individual, Fitness> getFitnesses() {
		Map<Individual, Fitness> toReturn = new IdentityHashMap<Individual, Fitness>();
		
		SimpleFitness[] fit = new SimpleFitness[2];
		fit[0] = new SimpleFitness();
		fit[1] = new SimpleFitness();
		fit[0].setFitness(null, payoffs[0], false);
		fit[1].setFitness(null, payoffs[1], false);
		
		toReturn.put((Individual)player[0], fit[0]);
		toReturn.put((Individual)player[1], fit[1]);
		
		return toReturn;
	}

	@Override
	public void setSeed(int seed) {
		random = new MersenneTwisterFast(seed);
	}

	@Override
	public void setGeneration(Integer generation) {
		this.generation = generation;
	}

	@Override
	public Integer getGeneration() {
		return generation;
	}

	@Override
	public void setSimulationID(Integer simulationID) {
		this.simulationID = simulationID;
	}

	@Override
	public Integer getSimulationID() {
		return simulationID;
	}

}
