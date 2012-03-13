package abce.io.simple;

import abce.agency.Market;
import abce.agency.consumer.Consumer;
import abce.agency.consumer.ReluctantSwitcher;
import abce.agency.engine.MarketSimulation;
import abce.agency.firm.Firm;
import abce.agency.firm.SimpleFirm;
import abce.agency.goods.DurableGood;
import abce.agency.goods.Good;
import abce.agency.production.ConstantCostProductionFunction;
import abce.agency.production.ProductionFunction;

public class SingleGoodSimulation extends MarketSimulation {
	private static final long serialVersionUID = 1L;

	private static final int testNumFirms = 1;
	private static final double testFirmEndowment = 1000000.0; // one MILLION DOLLARS!  (pinky)
	private static final int testNumConsumerAgents = 2;
	private static final int testNumPersonsPerConsumerAgent = 100;
	private static final double testWTP = 5.0;
	private static final double testConstantCost = 4.0;
	private static final double testConstantPrice = 4.5;
	private static final long testSeed = 12345;
	
	private Good g;
	private Market m;
	private ProductionFunction pf;
	
	public SingleGoodSimulation(long seed) {
		super(seed);
		
		g = new DurableGood("testgood");
		m = new Market(g);
		pf = new ConstantCostProductionFunction(testConstantCost);
		
		addMarket(m);
		
		// Add firms
		for (int i = 0; i < testNumFirms; i++) {
			Firm f = new SimpleFirm(testConstantPrice);
			f.grantEndowment(testFirmEndowment);
			f.startProducing(g, pf);
			forceMarketEntry(f, m);
			addFirm(f);
		}
		
		// Add consumers
		for (int i = 0; i < testNumConsumerAgents; i++) {
//			Consumer c = new PerfectlyRationalConsumer(testNumPersonsPerConsumerAgent);
			Consumer c = new ReluctantSwitcher(testNumPersonsPerConsumerAgent);
			c.enterMarket(m);
			c.setWTP(g, testWTP);
			addConsumer(c);
		}
	}
	
	public static void main(String[] args) {
		SingleGoodSimulation sgs = new SingleGoodSimulation(testSeed);
		for (int i = 0; i < 50; i++)
			sgs.schedule.step(sgs);
	}
	

}
