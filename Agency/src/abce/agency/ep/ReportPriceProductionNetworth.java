package abce.agency.ep;


import abce.agency.*;
import abce.agency.engine.*;
import abce.agency.firm.*;
import evoict.*;
import evoict.ep.*;
import evoict.io.*;



public class ReportPriceProductionNetworth implements Procedure {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	String						prefix;
	DelimitedOutFile			out;
	final String				format				= "Generation%d,SimulationID%d,Step%f,ConsumerID%d,GoodID%d,Price%f,LastProduction%f,Networth%f";
	boolean						untriggered			= true;



	@Override
	public void setup(EventProcedureArgs args) throws BadConfiguration {
		prefix = (args.containsKey("prefix") ? args.get("prefix") : "price_prod_nw");

	}



	@Override
	public void process(Object... context) throws Exception {
		MarketSimulation sim = (MarketSimulation) context[0];
		if (untriggered) {

			untriggered = false;
			String path = prefix + "-" + sim.generation + "-" + sim.simulationID + ".csv.gz";
			out = new DelimitedOutFile(path, format);
		}
		for (Firm f : sim.getFirms()) {
			for (Market m : sim.getMarkets()) {
				if (f.produces(m.good)) {
					out.write(sim.generation, sim.simulationID, sim.schedule.getTime(), f.agentID, m.good.id,
							f.getPrice(m, null),
							f.getLastProduction(m.good), f.getAccounts().getNetWorth());
				}
			}
		}
	}



	@Override
	public void finish() {
		out.close();
	}

}
