package abce.agency.events;


import abce.agency.Market;
import abce.agency.firm.Firm;
import abce.agency.util.BadConfiguration;
import abce.agency.util.io.DelimitedOutFile;
import abce.ecj.OligopolySimulation;



public class ReportPriceProductionNetworth implements Procedure {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	String						name				= "price+prod";
	final String				format				= "Generation%d,SimulationID%d,Step%f,FirmID%d,GoodID%d,Price%f,LastProduction%f,Networth%f";
	boolean						untriggered			= true;



	@Override
	public void setup(EventProcedureArgs args) throws BadConfiguration {
		name = (args.containsKey("name") ? args.get("name") : "price_prod_nw");

	}



	@Override
	public void process(Object... context) throws Exception {
		OligopolySimulation sim = (OligopolySimulation) context[0];
		String path = name + ".csv.gz";
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
