package abce.agency.ep;


import abce.agency.*;
import abce.agency.engine.*;
import abce.agency.firm.*;
import evoict.*;
import evoict.ep.*;
import evoict.io.*;



public class ReportPrice implements Procedure {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	boolean						untriggered			= true;
	DelimitedOutFile			out					= null;
	final String				format				= "Generation%d,SimulationID%d,Step%f,GoodID%d,MarketID%d,FirmID%d,Price%0.2f";
	String						prefix				= null;



	@Override
	public void setup(EventProcedureArgs args) throws BadConfiguration {
		prefix = (args.containsKey("prefix")) ? args.get("prefix") : "price";
	}



	@Override
	public void process(Object... context) throws Exception {
		MarketSimulation state = (MarketSimulation) context[0];
		if (untriggered) {
			untriggered = false;
			String path = prefix + "-" + state.generation + "-" + state.simulationID + ".csv.gz";
			out = new DelimitedOutFile(path, format);
		}
		for (Market m : state.getMarkets()) {
			for (Firm f : m.getFirms()) {
				// Price
				Double price = null;
				Offer o = f.getOffer(m.good, null);
				price = (o != null) ? price = o.price : Double.NaN;
				out.write(state.generation, state.simulationID, state.schedule.getTime(), m.good.id, m.id, f.agentID,
						price);
			}
		}
	}



	@Override
	public void finish() {
		if (out != null) {
			out.close();
		}

	}

}
