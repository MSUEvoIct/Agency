package abce.io.simple;


import java.io.*;

import abce.agency.*;
import abce.agency.engine.*;
import abce.agency.firm.*;
import abce.agency.goods.*;
import abce.agency.production.*;
import abce.io.simple.ecj.*;



public class ECSimpleMarketSimulation extends MarketSimulation
{

	private static final long			serialVersionUID	= 1L;

	public final Good					good;
	private final Market				m;
	private final ProductionFunction	pf;

	protected SimpleAgencyConfig		_config;
	protected Integer					_chunk				= null;
	protected Integer					_generation			= null;



	public ECSimpleMarketSimulation(long seed, String config_path, int chunk, int gen) {
		this(seed, null, null, config_path, chunk, gen);
	}



	public ECSimpleMarketSimulation(long seed, Integer generation, Integer simulationID, String config_path,
			int chunk, int gen) {
		super(seed);

		loadConfiguration(config_path);

		good = new DurableGood("testgood");
		m = new Market(good);
		pf = new ConstantCostProductionFunction(_config.cost_constant);
		_chunk = chunk;
		_generation = gen;

		addMarket(m);
	}



	public void setupFirm(Firm firm) {
		firm.grantEndowment(_config.firm_endowment);
		firm.startProducing(good, pf);
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
}
