package abce.agency;


import java.io.File;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import abce.agency.consumer.Consumer;
import abce.agency.events.EventProcedureDescription;
import abce.agency.events.Interval;
import abce.agency.events.MSEventProcedureManager;
import abce.agency.firm.Firm;
import abce.agency.util.BadConfiguration;
import abce.ecj.FileManager;



public class MarketSimulation extends SimState implements Callable {

	private static final long				serialVersionUID		= 1L;

	/**
	 * Each simulation per evolutionary run should be uniquely identified. Upon
	 * creation, this number should be used and incremented
	 */
	private static long						idSequence				= 0;

	// Lock for idSequence
	private static ReentrantReadWriteLock	idSequenceLock			= new ReentrantReadWriteLock();

	private static final long				defaultStepsToRun		= 30L;

	/**
	 * Default ordering for firms; firms must produce before consumers can
	 * purchase that production
	 */
	private static final int				defaultFirmOrdering		= 100;

	/**
	 * Default ordering for consumers; firms must produce before consumers can
	 * purchase that production
	 */
	private static final int				defaultConsumerOrdering	= 200;

	/**
	 * Default ordering for reporters and other non-simulation events
	 */
	private static final int				defaultEventOrdering	= 300;

	private long							stepsToRun				= defaultStepsToRun;

	// Substantive simulation state
	private final Set<Market>				markets					= new LinkedHashSet<Market>();
	private final Set<Firm>					firms					= new LinkedHashSet<Firm>();
	private final Set<Consumer>				consumers				= new LinkedHashSet<Consumer>();

	// For evolutionary simulations
	public Integer							generation;
	public final Long						simulationID;

	public File								simulationRoot;

	protected MSEventProcedureManager		event_manager			= new MSEventProcedureManager();
	public static FileManager				fm						= new FileManager();



	public MarketSimulation(long seed) {
		this(seed, defaultStepsToRun, null, null);
	}



	public MarketSimulation(long seed, long stepsToRun) {
		this(seed, stepsToRun, null, null);
	}



	public MarketSimulation(long seed, long stepsToRun, Integer generation,
			File simulationRoot) {

		super(seed);
		// override the default MASON Simulation Schedule
		this.schedule = new AsyncDataSchedule();

		this.generation = generation; // generation must be taken from EC system
		this.simulationID = MarketSimulation.nextID();

		if (simulationRoot != null)
			this.simulationRoot = simulationRoot;
		else
			// use the current directory if none specified
			this.simulationRoot = new File(System.getProperty("user.dir"));

	}



	public void addMarket(Market market) {
		this.markets.add(market);
		schedule.scheduleRepeating(Schedule.EPOCH, market);
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



	public void addEventProcedure(EventProcedureDescription desc) {
		try {
			event_manager.addEvent(desc);
		} catch (BadConfiguration e) {
			e.printStackTrace();
			System.exit(1);
		}
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



	/**
	 * Set the file simulation root
	 */
	public void setSimulationRoot(File root) {
		simulationRoot = root;
		if (!simulationRoot.exists()) {
			simulationRoot.mkdir();
		} else if (simulationRoot.exists() && !simulationRoot.isDirectory()) {
			System.err.println("Simulation root path already exists; unable to use as root directory.");
			System.exit(1);
		}
	}



	/**
	 * @return an immutable set of all consumers within the simulation.
	 */
	public Set<Consumer> getConsumers() {
		return Collections.unmodifiableSet(consumers);
	}



	/**
	 * @return an immutable set of all firms within the simulation
	 */
	public Set<Firm> getFirms() {
		return Collections.unmodifiableSet(firms);
	}



	/**
	 * @return an immutable set of all markets within the simulation
	 */
	public Set<Market> getMarkets() {
		return Collections.unmodifiableSet(markets);
	}



	@Override
	public Integer call() throws Exception {
		for (int i = 0; i < stepsToRun; i++) {
			// Time Step Starts

			// Scheduled Executes
			schedule.step(this);

			// Time Step Ends
			event_manager.process(MSEventProcedureManager.EVENT_STEP, schedule.time(), this);
		}
		event_manager.process(MSEventProcedureManager.EVENT_STEP, Interval.ATEND, this);
		event_manager.finish();
		return 0;
	}



	/**
	 * Return the next sequence ID and increment the counter
	 * 
	 * @return
	 */
	private static long nextID() {
		idSequenceLock.writeLock().lock();
		try {
			return idSequence++;
		} finally {
			idSequenceLock.writeLock().unlock();
		}
	}



	/**
	 * Return the number of IDs issued.
	 * 
	 * @return
	 */
	public static long numIssuedIDs() {
		idSequenceLock.readLock().lock();
		try {
			return idSequence;
		} finally {
			idSequenceLock.readLock().unlock();
		}
	}

}
