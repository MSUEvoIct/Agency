package abce.agency.engine;


import java.io.*;
import java.util.*;

import sim.engine.*;
import abce.agency.*;
import abce.agency.consumer.*;
import abce.agency.firm.*;



public class MarketSimulation extends SimState implements Runnable {

	private static final long	serialVersionUID		= 1L;
	private static final long	defaultStepsToRun		= 30L;
	private static final int	defaultFirmOrdering		= 100;						// default
																					// ordering
																					// for
																					// firms
	private static final int	defaultConsumerOrdering	= 200;						// default
																					// ordering
																					// for
																					// consumers
	private static final int	defaultEventOrdering	= 300;						// default
																					// ordering
																					// for
																					// data
																					// reporters

	private long				stepsToRun				= defaultStepsToRun;

	// Substantive simulation state
	private final Set<Market>	markets					= new HashSet<Market>();
	private final Set<Firm>		firms					= new HashSet<Firm>();
	private final Set<Consumer>	consumers				= new HashSet<Consumer>();

	// For evolutionary simulations
	public final Integer		generation;
	public final Integer		simulationID;

	public final File			simulationRoot;



	public MarketSimulation(long seed) {
		this(seed, defaultStepsToRun, null, null, null);
	}



	public MarketSimulation(long seed, long stepsToRun) {
		this(seed, stepsToRun, null, null, null);
	}



	public MarketSimulation(long seed, long stepsToRun, Integer generation,
			Integer simulationID, File simulationRoot) {

		super(seed);
		// override the default MASON Simulation Schedule
		this.schedule = new AsyncDataSchedule();

		this.generation = generation;
		this.simulationID = simulationID;
		if (simulationRoot != null)
			this.simulationRoot = simulationRoot;
		else
			// use the current directory if none specified
			this.simulationRoot = new File(System.getProperty("user.dir"));

	}



	public void addMarket(Market market) {
		this.markets.add(market);
	}



	public void addEvent(Steppable event) {
		schedule.scheduleRepeating(Schedule.EPOCH, defaultEventOrdering, event);
	}



	/**
	 * Add a firm to the simulation. Note that the firm does not automatically
	 * enter the market. See Firm.enter(Market m).
	 * 
	 * @param firm
	 */
	public void addFirm(Firm firm) {
		this.firms.add(firm);
		schedule.scheduleRepeating(Schedule.EPOCH, defaultFirmOrdering, firm);
	}



	public void addConsumer(Consumer consumer) {
		this.consumers.add(consumer);
		schedule.scheduleRepeating(Schedule.EPOCH, defaultConsumerOrdering, consumer);
	}



	/**
	 * If a firm does not enter markets itself, modelers may wish to force a
	 * static
	 * market situation and thus the market entry of a specific firm.
	 * 
	 * @param firm
	 * @param market
	 */
	public void forceMarketEntry(Firm firm, Market market) {
		firm.enter(market);
	}



	/**
	 * @return The number of steps that have been executed so far.
	 */
	public long getSteps() {
		return schedule.getSteps();
	}



	/**
	 * Set the number of steps to run the simulation for.
	 * 
	 * @param steps
	 *            number of steps
	 */
	public void setStepsToRun(long steps) {
		stepsToRun = steps;
	}



	public Set<Consumer> getConsumers() {
		return Collections.unmodifiableSet(consumers);
	}



	@Override
	public void run() {
		for (int i = 0; i < stepsToRun; i++) {
			// Time Step Starts

			// Scheduled Executes
			schedule.step(this);

			// Time Step Ends
		}
	}

}
