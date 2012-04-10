package abce.io.simple;


import java.io.FileNotFoundException;
import java.io.IOException;

import abce.agency.Market;
import abce.agency.consumer.Consumer;
import abce.agency.consumer.PerfectlyRationalConsumer;
import abce.agency.engine.MarketSimulation;
import abce.agency.firm.ECJSimpleFirm;
import abce.agency.firm.Firm;
import abce.agency.goods.DurableGood;
import abce.agency.goods.Good;
import abce.agency.production.ConstantCostProductionFunction;
import abce.agency.production.ProductionFunction;
import abce.io.simple.ecj.SimpleAgencyConfig;



public class ECSimpleMarketSimulation extends MarketSimulation {
	private static final long			serialVersionUID	= 1L;
	
	
	public final Good					good;
	private final Market				m;
	private final ProductionFunction	pf;

	protected SimpleAgencyConfig		_config;


	public ECSimpleMarketSimulation(long seed, String config_path, int chunk, int gen) {
		this(seed, null, null, config_path, chunk, gen);
	}



	public ECSimpleMarketSimulation(long seed, Integer generation, Integer simulationID, String config_path,
			int chunk, int gen) {
		super(seed);

		loadConfiguration(config_path);

		/*
		 * TODO-MATT:  Why must this be determined here, rather than before this function is called?  Generation
		 * and simulaiton ID are final because they should be assigned once and then never change (i.e., the definition
		 * of final) 
		 */
		super.setStepsToRun(_config.steps_to_run); 
		
		/*
		 * TODO-MATT: This is set in the constructor, and that constructor
		 * should be the one called above. I make generation non-final to make
		 * this work here as a kludge.
		 */
		super.generation = gen;

		good = new DurableGood("testgood");
		m = new Market(good);
		pf = new ConstantCostProductionFunction(_config.cost_constant);

		addMarket(m);

		// Add consumers
		for (int i = 0; i < _config.number_of_customers; i++) {
			Consumer c = new PerfectlyRationalConsumer(_config.persons_per_consumer_agent);
			// Consumer c = new
			// ReluctantSwitcher(_config.persons_per_consumer_agent);
			c.enterMarket(m);
			c.setWTP(good, _config.willingness_to_pay);
			addConsumer(c);
		}
	}

	public void setupFirm(Firm firm) {
		firm.grantEndowment(_config.firm_endowment);
		firm.startProducing(good, pf);
		((ECJSimpleFirm) firm).setPrice(_config.firm_initial_price);
		forceMarketEntry(firm, m);
		addFirm(firm);
	}



	public void loadConfiguration(String config_path) {
		_config = null;
		try {
			_config = new SimpleAgencyConfig(config_path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}



	public SimpleAgencyConfig getConfig() {
		return _config;
	}



	@Override
	public void run() {
		// SimpleConsumerReporter scr = new SimpleConsumerReporter(new
		// PrintWriter(System.out), false, 1, this);
		// this.addEvent(scr);
		super.run();
	}
}
