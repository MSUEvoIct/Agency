package abce.ecj;


import java.io.*;

import abce.agency.*;
import abce.agency.consumer.*;
import abce.agency.engine.*;
import abce.agency.firm.*;
import abce.agency.goods.*;
import abce.agency.production.*;
import evoict.ep.*;



public class OligopolySimulation extends MarketSimulation {

	private static final long			serialVersionUID	= 1L;

	public final Good					good;
	private final Market				m;
	private final ProductionFunction	pf;

	protected OligopolyConfig			_config;



	public OligopolySimulation(long seed, String config_path, int chunk, int gen) {

		super(seed);

		loadConfiguration(config_path);

		// Have to set this here because configuration needs to load after the
		// call to the parent constructor.
		super.setStepsToRun(_config.steps_to_run);
		super.generation = gen;
		super.setSimulationRoot(new File(_config.simulation_root));

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



	public void setupEvents(EventProcedureDescription[] events) {
		for (EventProcedureDescription desc : events) {
			addEventProcedure(desc);
		}
	}



	public void setupFirm(Firm f) {
		f.grantEndowment(_config.firm_endowment);
		f.startProducing(good, pf);
		f.setPrice(good, _config.firm_initial_price);

		// TODO: How should last production be seeded?
		// f.setLastProduction(good, _config.firm_initial_production);
		forceMarketEntry(f, m);
		addFirm(f);
	}



	public void loadConfiguration(String config_path) {
		_config = null;
		try {
			_config = new OligopolyConfig(config_path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}



	public OligopolyConfig getConfig() {
		return _config;
	}



	@Override
	public void run() {
		super.run();
	}
}
