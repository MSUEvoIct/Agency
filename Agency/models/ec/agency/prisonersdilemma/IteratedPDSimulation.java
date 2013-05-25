package ec.agency.prisonersdilemma;

import ec.agency.io.DelimitedOutFile;
import ec.agency.io.FileManager;
import ec.util.MersenneTwisterFast;

public class IteratedPDSimulation implements Runnable {
	private static final long serialVersionUID = 1L;
	
	public static FileManager fm = new FileManager();
	static {
		fm = new FileManager();
		try {
			fm.initialize(".");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// number of steps for which to keep a decision history
	public static final int historySize = 5;

	public final String productionFormat = "Generation%d,SimulationID%d,Agent%d,TimesDefected%d";
	public final String productionFile = "iterated_pd.csv.gz";
	public final int stepsToRun;
	public int step = 0;

	public final MersenneTwisterFast random;

	// Game outcome variables
	public final double payoffBothCooperate;
	public final double payoffWinner;
	public final double payoffLoser;
	public final double payoffBothDefect;

	// Participating agents
	public IteratedPDAgent first, second;

	// Details on agent behavior
	public double earningsFirst = 0.0, earningsSecond = 0.0;
	public boolean[] firstDefections, secondDefections;

	public int simulationID;
	public int generation;

	public IteratedPDSimulation(long seed, int stepsToRun, IteratedPDAgent first,
			IteratedPDAgent second, double payoffBothCooperate,
			double payoffWinner, double payoffLoser, double payoffBothDefect) {
		random = new MersenneTwisterFast(seed);
		
		this.stepsToRun = stepsToRun;
		
		this.first = first;
		this.second = second;
		
		this.payoffBothCooperate = payoffBothCooperate;
		this.payoffWinner = payoffWinner;
		this.payoffLoser = payoffLoser;
		this.payoffBothDefect = payoffBothDefect;
		
		firstDefections = new boolean[stepsToRun];
		secondDefections = new boolean[stepsToRun];
	}

	public void run() {
		for (int i = 0; i < this.stepsToRun; i++) {

			// Ask agents whether or not they're defecting
			boolean firstDefected = firstDefections[step] = first.defect(this);
			boolean secondDefected = secondDefections[step] = second.defect(this);

			// Assign earnings for each of the four possible outcomes.
			if (firstDefected && secondDefected) {
				earningsFirst += payoffBothDefect;
				earningsSecond += payoffBothDefect;
			} else if (firstDefected) {
				earningsFirst += payoffWinner;
				earningsSecond += payoffLoser;
			} else if (secondDefected) {
				earningsFirst += payoffLoser;
				earningsSecond += payoffWinner;
			} else {
				earningsFirst += payoffBothCooperate;
				earningsSecond += payoffBothCooperate;
			}

			// increment the step counter
			step++;

		}

		// --- Simulation is now finished ---
		// track outputs
		if ((generation % 10 == 0) && (simulationID % 97 == 0)) {
			try {
				DelimitedOutFile cooperation = fm.getDelimitedOutFile(
						productionFile, productionFormat);
				cooperation.write(generation, simulationID, 0,
						timesDefected(first));
				cooperation.write(generation, simulationID, 1,
						timesDefected(second));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public int timesDefected(IteratedPDAgent agent) {
		boolean[] defections;
		if (agent == first)
			defections = firstDefections;
		else if (agent == second) 
			defections = secondDefections;
		else
			throw new IllegalArgumentException();
		
		int times = 0;
		for (int i = 0; i < step; i++) {
			if (defections[i])
				times++;
		}
		return times;
	}
	
	
	public IteratedPDAgent getOtherAgent(IteratedPDAgent calling) {
		if (calling == first)
			return second;
		else if (calling == second)
			return first;
		else
			throw new RuntimeException(
					"This function needs to be called by one of the agents!");
	}

	public boolean[] getDefections(IteratedPDAgent forAgent) {
		if (forAgent == first) 
			return firstDefections;
		else if (forAgent == second)
			return secondDefections;
		else
			throw new IllegalArgumentException();
	}
	
}
