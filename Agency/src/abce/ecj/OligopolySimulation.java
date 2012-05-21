package abce.ecj;


import java.io.*;

import abce.agency.*;
import abce.agency.consumer.*;
import abce.agency.engine.*;
import abce.agency.firm.*;
import abce.agency.goods.*;
import abce.agency.production.*;
import evoict.ep.*;



/**
 * The Oligopoly simulation is the main entry part for the Oligopoly domain
 * model. The ECJ calls the constructor with the random number seed to
 * initialize the simulation and load the default configuration settings from
 * file. A subsequent all to initialize() finishes the configuration process.
 * Changes to the configuration can be made between these times by modifying
 * settings in the config object. After initialize() is finished, many changes
 * to configuration settings may cause undefined behaviors.
 * 
 * setupEvents and setupFirms setup those respective objects.
 * 
 * The main entry point for the model is through the call() method.
 * 
 * @author ruppmatt
 * 
 */
public class OligopolySimulation extends MarketSimulation {

	private static final long	serialVersionUID	= 1L;

	public final Good			good;
	public final Market			m;
	public ProductionFunction	pf;

	protected OligopolyConfig	_config;



	public OligopolySimulation(long seed, String config_path, int gen) {
		super(seed);
		super.generation = gen;

		good = new DurableGood("testgood");
		m = new Market(good);

		loadConfiguration(config_path);
	}



	/**
	 * Initialize should be called after all changes to the configuration
	 * settings are made. Once this method is called, the settings in the
	 * configuration object are used to build other objects in the model. Any
	 * subsequent changes to configuration after initialize() returns results in
	 * undefined behavior in many cases.
	 */
	public void initialize() {
		_config.register();
		super.setStepsToRun(_config.steps_to_run);
		super.setSimulationRoot(new File(_config.simulation_root));

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



	/**
	 * Setup events for the domain model
	 * 
	 * @param events
	 */
	public void setupEvents(EventProcedureDescription[] events) {
		for (EventProcedureDescription desc : events) {
			addEventProcedure(desc);
		}
	}



	/**
	 * Setup firms in the domain model.
	 * 
	 * @param f
	 */
	public void setupFirm(Firm f) {
		f.grantEndowment(_config.firm_endowment);
		f.startProducing(good, pf);
		f.setPrice(good, _config.firm_initial_price);

		// TODO: How should last production be seeded?
		// f.setLastProduction(good, _config.firm_initial_production);
		forceMarketEntry(f, m);
		addFirm(f);
	}



	/**
	 * Load the configuration file
	 * 
	 * @param config_path
	 */
	protected void loadConfiguration(String config_path) {
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



	/**
	 * return the configuration object
	 */
	public OligopolyConfig getConfig() {
		return _config;
	}



	@Override
	public Integer call() throws Exception {
		return super.call();
	}
}
