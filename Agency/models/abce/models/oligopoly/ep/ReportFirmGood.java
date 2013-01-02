package abce.models.oligopoly.ep;


import ec.agency.Market;
import ec.agency.events.EventProcedureArgs;
import ec.agency.events.Procedure;
import ec.agency.io.DelimitedOutFile;
import ec.agency.util.BadConfiguration;
import abce.agency.firm.Firm;
import abce.models.oligopoly.OligopolySimulation;



public class ReportFirmGood implements Procedure {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	String						name				= "firm_goods";
	final String				format				= "Generation%d,SimulationID%d,Step%f,FirmID%d,GoodID%d,Price%f,Production%f,Inventory%f,QtySold%f,Revenue%f";
	boolean						untriggered			= true;



	@Override
	public void setup(EventProcedureArgs args) throws BadConfiguration {
		name = args.containsKey("name") ? args.get("name") : name;
	}



	@Override
	public void process(Object... context) throws Exception {
		OligopolySimulation sim = (OligopolySimulation) context[0];
		String path = name + ".csv.gz";
		DelimitedOutFile out = sim.file_manager.getDelimitedOutFile(path, format);
		
		for (Firm f : sim.getFirms()) {
			for (Market m : sim.getMarkets()) {
				if (f.hasProduced(m.good)) {
					out.write(
							sim.generation, 
							sim.simulationID, 
							sim.schedule.getTime(),
							f.agentID, 
							m.good.id,
							f.getPrice(m.good, null, 0),
							f.getProduction(m.good, 0), 
							f.getInventory(m.good,  0),
							f.getSales(m.good, 0),
							f.getRevenue(m.good, 0)
							);
				}
			}
		}
	}



	@Override
	public void finish() {
	}
}
