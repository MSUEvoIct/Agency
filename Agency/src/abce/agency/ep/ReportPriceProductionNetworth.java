package abce.agency.ep;


import abce.agency.*;
import abce.agency.firm.*;
import abce.ecj.*;
import evoict.*;
import evoict.ep.*;
import evoict.io.*;



public class ReportPriceProductionNetworth implements Procedure {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	String						name				= "price+prod";
	final String				format				= "Generation%d,SimulationID%d,Step%f,ConsumerID%d,GoodID%d,Price%f,LastProduction%f,Networth%f";
	boolean						untriggered			= true;



	@Override
	public void setup(EventProcedureArgs args) throws BadConfiguration {
		name = (args.containsKey("name") ? args.get("name") : "price_prod_nw");

	}



	@Override
	public void process(Object... context) throws Exception {
		OligopolySimulation sim = (OligopolySimulation) context[0];
		String dir = sim.simulationRoot.getPath();
		String path = dir + "/" + name + ".csv.gz";
		DelimitedOutFile out = sim.file_manager.getDelimitedOutFile(path, format);
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
	}
}
