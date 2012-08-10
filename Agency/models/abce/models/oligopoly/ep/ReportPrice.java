package abce.models.oligopoly.ep;


import abce.agency.Market;
import abce.agency.Offer;
import abce.agency.firm.Firm;
import abce.util.io.DelimitedOutFile;
import abce.util.events.EventProcedureArgs;
import abce.util.events.Procedure;
import abce.models.oligopoly.OligopolySimulation;
import abce.util.BadConfiguration;



public class ReportPrice implements Procedure {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	final String				format				= "Generation%d,SimulationID%d,Step%.1f,GoodID%d,MarketID%d,FirmID%d,Price%.2f";
	String						name				= null;



	@Override
	public void setup(EventProcedureArgs args) throws BadConfiguration {
		name = (args.containsKey("name")) ? args.get("name") : "price";
	}



	@Override
	public void process(Object... context) throws Exception {
		OligopolySimulation state = (OligopolySimulation) context[0];
		String path = name + ".csv.gz";
		DelimitedOutFile out = state.file_manager.getDelimitedOutFile(path, format);
		for (Market m : state.getMarkets()) {
			for (Firm f : m.getFirms()) {
				// Price
				Double price = null;
				Offer o = f.getOffer(m, null);
				price = (o != null) ? price = o.price : Double.NaN;
				out.write(state.generation, state.simulationID, state.schedule.getTime(), m.good.id, m.id, f.agentID,
						price);
			}
		}
	}



	@Override
	public void finish() {
	}

}
