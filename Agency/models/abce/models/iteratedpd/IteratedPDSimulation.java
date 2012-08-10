package abce.models.iteratedpd;

import java.util.HashMap;
import java.util.Map;

import sim.engine.SimState;
import abce.agency.Agent;
import abce.util.io.DelimitedOutFile;
import abce.util.io.FileManager;

public class IteratedPDSimulation extends SimState implements Runnable {
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
	
	public final String			productionFormat				= "Generation%d,SimulationID%d,Agent%d,TimesDefected%d";
	public final String	productionFile = "iterated_pd.csv.gz";
	public int steps;
	
	public final double payoffBothCooperate;
	public final double payoffWinner;
	public final double payoffLoser;
	public final double payoffBothDefect;
	
	public final Map<IteratedPDAgent,boolean[]> defections = new HashMap<IteratedPDAgent,boolean[]>();
	public final Map<IteratedPDAgent,Integer> totalTimesDefected = new HashMap<IteratedPDAgent,Integer>();
		
	IteratedPDAgent first, second;
	public int simulationID;
	public int generation;

	public IteratedPDSimulation(long seed, IteratedPDAgent first,
			IteratedPDAgent second, double payoffBothCooperate,
			double payoffWinner, double payoffLoser, double payoffBothDefect) {
		super(seed);
		
		// keep references to each agent and put them in the schedule
		this.first = first;
		this.second = second;
		this.schedule.scheduleRepeating(first);
		this.schedule.scheduleRepeating(second);
		
		this.payoffBothCooperate = payoffBothCooperate;
		this.payoffWinner = payoffWinner;
		this.payoffLoser = payoffLoser;
		this.payoffBothDefect = payoffBothDefect;
	
		defections.put(first, new boolean[Agent.trackingPeriods]);
		defections.put(second, new boolean[Agent.trackingPeriods]);

		totalTimesDefected.put(first, 0);
		totalTimesDefected.put(second, 0);
		
	}

	public void run() {
		for(int i = 0; i < steps; i++){
			
			// Let agents step() and make decisions
			schedule.step(this);
			
			boolean firstDefected = first.defected(this, 0);
			if (firstDefected) {
				int timesDefected = this.totalTimesDefected.get(first);
				timesDefected++;
				this.totalTimesDefected.put(first, timesDefected);
			}

			boolean secondDefected = second.defected(this, 0);
			if (secondDefected) {
				int timesDefected = this.totalTimesDefected.get(second);
				timesDefected++;
				this.totalTimesDefected.put(second, timesDefected);
			}
			
			if (firstDefected && secondDefected) {
				first.earn(payoffBothDefect);
				second.earn(payoffBothDefect);
			} else if (firstDefected) {
				first.earn(payoffWinner);
				second.earn(payoffLoser);
			} else if (secondDefected) {
				first.earn(payoffLoser);
				second.earn(payoffWinner);
			} else {
				first.earn(payoffBothCooperate);
				second.earn(payoffBothCooperate);
			}
		}
		
		// track outputs
		if ((generation % 10 == 0) && (simulationID % 97 == 0)) {
			try {
				DelimitedOutFile cooperation = fm.getDelimitedOutFile(productionFile, productionFormat);
				cooperation.write(generation,simulationID, 0, totalTimesDefected.get(first) );
				cooperation.write(generation,simulationID, 1, totalTimesDefected.get(second) );
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		
	}

	public IteratedPDAgent getOtherAgent(IteratedPDAgent calling) {
		if (calling == first)
			return second;
		else if (calling == second) 
			return first;
		else
			throw new RuntimeException("This function needs to be called by one of the agents!");
	}
	

	
}
