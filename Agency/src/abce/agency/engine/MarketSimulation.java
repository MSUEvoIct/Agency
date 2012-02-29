package abce.agency.engine;

import java.util.HashSet;
import java.util.Set;

import abce.agency.Market;
import abce.agency.consumer.Consumer;
import abce.agency.firm.Firm;

import sim.engine.Schedule;
import sim.engine.SimState;

public class MarketSimulation extends SimState {
	private static final long serialVersionUID = 1L;
	
	private static final int defaultFirmOrdering = 100; // default ordering for firms
	private static final int defaultConsumerOrdering = 200; // default ordering for consumers
	
	private Set<Market> markets = new HashSet<Market>();
	private Set<Firm> firms = new HashSet<Firm>();
	private Set<Consumer> consumers = new HashSet<Consumer>();
	
	public MarketSimulation(long seed) {
		super(seed);
		schedule = new AsyncDataSchedule();
		// TODO Auto-generated constructor stub
	}

	public void addMarket(Market market) {
		this.markets.add(market);
	}
	
	/**
	 * Add a firm to the simulation.  Note that the firm does not automatically
	 * enter the market.  See Firm.enter(Market m).
	 * 
	 * @param firm
	 */
	public void addFirm(Firm firm) {
		this.firms.add(firm);
		schedule.scheduleRepeating(Schedule.EPOCH, defaultFirmOrdering, firm);
	}
	
	public void addConsumer(Consumer consumer) {
		this.consumers.add(consumer);
		schedule.scheduleRepeating(Schedule.EPOCH,defaultConsumerOrdering,consumer);
	}
	
	/**
	 * If a firm does not enter markets itself, modelers may wish to force a static
	 * market situation and thus the market entry of a specific firm.
	 * 
	 * @param firm
	 * @param market
	 */
	public void forceMarketEntry(Firm firm, Market market) {
		firm.enter(market);
	}
	
	

	
	
}
