package abce.io.simple;


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



	public ECSimpleMarketSimulation(long seed, SimpleAgencyConfig config, int chunk, int gen) {
		this(seed, null, null, config, chunk, gen);
	}



	public ECSimpleMarketSimulation(long seed, Integer generation, Integer simulationID, SimpleAgencyConfig config,
			int chunk, int gen) {
		super(seed);

		good = new DurableGood("testgood");
		m = new Market(good);
		pf = new ConstantCostProductionFunction(config.cost_constant);
		_chunk = chunk;
		_generation = gen;
		_config = config;

		addMarket(m);
	}



	public void setupFirm(Firm firm) {
		firm.grantEndowment(_config.firm_endowment);
		firm.startProducing(good, pf);
		forceMarketEntry(firm, m);
		addFirm(firm);
	}



	public SimpleAgencyConfig getConfig() {
		return _config;
	}
}
