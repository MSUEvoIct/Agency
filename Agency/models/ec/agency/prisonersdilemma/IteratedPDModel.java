package ec.agency.prisonersdilemma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class IteratedPDModel implements AgencyModel {
	private static final long serialVersionUID = 1L;

	static final String P_STEPS = "steps";
	static final String P_PAYOFF_COOPERATE = "payoffBothCooperate";
	static final String P_PAYOFF_WINNER = "payoffWinner";
	static final String P_PAYOFF_LOSER = "payoffLoser";
	static final String P_PAYOFF_DEFECT = "payoffBothDefect";

	static final Lock openOutputLock = new ReentrantLock();
	static DataOutputFile outFile;

	// Operational Details
	MersenneTwisterFast random;
	int simulationID;
	int generation;
	int stepsToRun;
	int step = 0;

	// Game outcome variables
	double payoffBothCooperate;
	double payoffWinner;
	double payoffLoser;
	double payoffBothDefect;

	// Participating agents and variables
	Prisoner[] prisoners = new Prisoner[2];
	double[] payoffs = new double[2];
	int numCooperated = 0;
	int numWinnerLoser = 0;
	int numDefected = 0;

	@SuppressWarnings("unchecked")
	// Generics not supported with arrays
	List<Boolean>[] defections = new ArrayList[2];

	public IteratedPDModel() {
		openOutputLock.lock();
		try {
			if (outFile == null) {
				String[] colNames = new String[7];
				colNames[0] = "Generation";
				colNames[1] = "SimulationID";
				colNames[2] = "Cooperated";
				colNames[3] = "WinnerLoser";
				colNames[4] = "Defected";
				colNames[5] = "TotalPayoff";
				colNames[6] = "PayoffDifference";

				outFile = new GenerationAggregatingDataOutputFile("PrisonersDilemmaResults.tsv",
						colNames);
			}
		} finally {
			openOutputLock.unlock();
		}

	}

	@Override
	public void setup(EvolutionState evoState, Parameter base) {
		ParameterDatabase pd = evoState.parameters;
		this.stepsToRun = pd.getInt(base.push(P_STEPS), null);
		this.payoffBothCooperate = pd.getDouble(base.push(P_PAYOFF_COOPERATE),
				null);
		this.payoffWinner = pd.getDouble(base.push(P_PAYOFF_WINNER), null);
		this.payoffLoser = pd.getDouble(base.push(P_PAYOFF_LOSER), null);
		this.payoffBothDefect = pd.getDouble(base.push(P_PAYOFF_DEFECT), null);

		defections[0] = new ArrayList<Boolean>(stepsToRun);
		defections[1] = new ArrayList<Boolean>(stepsToRun);
	}

	@Override
	public void run() {

		/*
		 * B/c/o the way the data is stored, stimuli are always the same. Avoid
		 * recreating them, but remember to update the step.
		 */
		InterrogationStimulus[] stimuli = new InterrogationStimulus[2];
		stimuli[0] = new InterrogationStimulus();
		stimuli[1] = new InterrogationStimulus();

		stimuli[0].myPlays = defections[0];
		stimuli[0].opponentsPlays = defections[1];
		stimuli[1].myPlays = defections[1];
		stimuli[1].opponentsPlays = defections[0];

		for (step = 0; step < stepsToRun; step++) {
			// Do not update until both have decided
			boolean[] defected = new boolean[2];

			stimuli[0].step = step;
			stimuli[1].step = step;
			defected[0] = prisoners[0].defect(stimuli[0]);
			defected[1] = prisoners[1].defect(stimuli[1]);

			defections[0].add(defected[0]);
			defections[1].add(defected[1]);

			if (defected[0] && defected[1]) {
				payoffs[0] += payoffBothDefect;
				payoffs[1] += payoffBothDefect;
				numDefected++;
			} else if (defected[0] && !defected[1]) {
				payoffs[0] += payoffWinner;
				payoffs[1] += payoffLoser;
				numWinnerLoser++;
			} else if (!defected[0] && defected[1]) {
				payoffs[0] += payoffLoser;
				payoffs[1] += payoffWinner;
				numWinnerLoser++;
			} else if (!defected[0] && !defected[1]) {
				payoffs[0] += payoffBothCooperate;
				payoffs[1] += payoffBothCooperate;
				numCooperated++;
			}
		}

		// Data Output
		Object[] data = new Object[7];
		data[0] = this.generation;
		data[1] = this.simulationID;
		data[2] = this.numCooperated;
		data[3] = this.numWinnerLoser;
		data[4] = this.numDefected;
		data[5] = payoffs[0] + payoffs[1];
		data[6] = Math.abs(payoffs[0] - payoffs[1]);
		outFile.writeTuple(data);

	}

	@Override
	public void setEvaluationGroup(EvaluationGroup evalGroup) {
		/*
		 * This group must contain exactly two individuals, each of which
		 * implement the IteratedPDAgent interface.
		 */
		if (evalGroup.individuals.size() != 2) {
			throw new RuntimeException("Must eval exactly two individuals");
		}

		for (int i = 0; i < 2; i++) {
			if (!(evalGroup.individuals.get(i) instanceof Prisoner))
				throw new RuntimeException("Incorrect agent type");
			prisoners[i] = (Prisoner) evalGroup.individuals.get(i);
		}
	}

	@Override
	public void setSeed(int seed) {
		this.random = new MersenneTwisterFast(seed);
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
		return this.simulationID;
	}

	@Override
	public Map<Individual, Fitness> getFitnesses() {
		Map<Individual, Fitness> toReturn = new HashMap<Individual, Fitness>();
		Individual[] inds = new Individual[2];
		inds[0] = (Individual) prisoners[0];
		inds[1] = (Individual) prisoners[1];

		SimpleFitness[] fit = new SimpleFitness[2];
		fit[0] = new SimpleFitness();
		fit[1] = new SimpleFitness();
		fit[0].setFitness(null, (float) payoffs[0], false);
		fit[1].setFitness(null, (float) payoffs[1], false);

		toReturn.put(inds[0], fit[0]);
		toReturn.put(inds[1], fit[1]);

		return toReturn;
	}

}
