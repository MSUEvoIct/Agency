package abce.models.io.simple;


import java.io.PrintWriter;

import abce.agency.Market;
import abce.agency.MarketSimulation;
import abce.agency.consumer.Consumer;
import abce.agency.consumer.ReluctantSwitcher;
import abce.agency.firm.Firm;
import abce.agency.firm.FixedPricingFirm;
import abce.agency.goods.DurableGood;
import abce.agency.goods.Good;
import abce.agency.production.ConstantCostProductionFunction;
import abce.agency.production.ProductionFunction;



public class SingleGoodSimulation extends MarketSimulation {

	private static final long			serialVersionUID				= 1L;

	private static final int			testNumFirms					= 1;
	private static final double			testFirmEndowment				= 1000000.0;	// one
																						// MILLION
																						// DOLLARS!
																						// (pinky)
	private static final int			testNumConsumerAgents			= 2;
	private static final int			testNumPersonsPerConsumerAgent	= 100;
	private static final double			testWTP							= 5.0;
	private static final double			testConstantCost				= 4.0;
	private static final double			testConstantPrice				= 4.5;
	private static final long			testSeed						= 12345;

	public final Good					good;

	private final Market				m;
	private final ProductionFunction	pf;



	public SingleGoodSimulation(long seed) {
		this(seed, null, null);
	}



	public SingleGoodSimulation(long seed, Integer generation, Integer simulationID) {
		super(seed);

		good = new DurableGood("testgood");
		m = new Market(good);
		pf = new ConstantCostProductionFunction(testConstantCost);

		addMarket(m);

		// Add firms
		for (int i = 0; i < testNumFirms; i++) {
			Firm f = new FixedPricingFirm(testConstantPrice);
			f.setPrice(good, testConstantPrice);
			f.grantEndowment(testFirmEndowment);
			f.startProducing(good, pf);
			forceMarketEntry(f, m);
			addFirm(f);
		}

		// Add consumers
		for (int i = 0; i < testNumConsumerAgents; i++) {
			// Consumer c = new
			// PerfectlyRationalConsumer(testNumPersonsPerConsumerAgent);
			Consumer c = new ReluctantSwitcher(testNumPersonsPerConsumerAgent);
			c.enterMarket(m);
			c.setWTP(good, testWTP);
			addConsumer(c);
		}
	}



	public static void main(String[] args) {
		SingleGoodSimulation sgs = new SingleGoodSimulation(testSeed);
		SimpleConsumerReporter scr = new SimpleConsumerReporter(new PrintWriter(System.out), false, 1, sgs);
		sgs.addEvent(scr);
		try {
			sgs.call();
		} catch (Exception e) {
			System.exit(1);
		}
	}

}
