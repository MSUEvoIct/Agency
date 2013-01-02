package abce.models.oligopoly.ep;

import ec.agency.Market;
import ec.agency.events.EventProcedureArgs;
import ec.agency.events.Procedure;
import ec.agency.io.DelimitedOutFile;
import ec.agency.util.BadConfiguration;
import abce.agency.firm.Firm;
import abce.models.oligopoly.OligopolySimulation;

public class ReportFirm implements Procedure {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String	name = "firm_account";
	final String format = "Generation%d,SimulationID%d,Step%f,FirmID%d,Cash%f,Debt%f,AssetsValue%f,AvailFinancing%f,Networth%f";

	@Override
	public void setup(EventProcedureArgs args) throws BadConfiguration {
		name = args.containsKey("name") ? args.get("name") : name;
	}

	@Override
	public void process(Object... context) throws Exception {
		OligopolySimulation sim = (OligopolySimulation) context[0];
		String path = name + ".csv.gz";
		DelimitedOutFile out = sim.file_manager.getDelimitedOutFile(path, format);
		
		for (Firm f : sim.getFirms()){
			for (Market m : sim.getMarkets()){
				if (f.hasProduced(m.good)){
					out.write(
							sim.generation,
							sim.simulationID,
							sim.schedule.getTime(),
							f.agentID,
							f.getAccounts().getCashOnHand(),
							f.getAccounts().getDebtBalance(),
							f.getAccounts().getAssetsValue(),
							f.getAccounts().getAvailableFinancing(),
							f.getAccounts().getNetWorth()
							);
					
				}
			}
		}
		
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}

	
}
