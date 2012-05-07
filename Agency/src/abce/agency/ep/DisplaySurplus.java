package abce.agency.ep;


import abce.agency.*;
import abce.agency.engine.*;
import abce.agency.firm.*;
import evoict.*;
import evoict.ep.*;



public class DisplaySurplus implements Procedure {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;



	@Override
	public void setup(EventProcedureArgs args) throws BadConfiguration {
	}



	@Override
	public void process(Object... context) throws Exception {
		MarketSimulation market = (MarketSimulation) context[0];
		StringBuffer buf = new StringBuffer();
		buf.append(market.simulationID + " " + market.schedule.getTime() + " ");
		for (Market m : market.getMarkets()) {
			double total_inventory = 0.0;
			for (Firm f : market.getFirms()) {
				total_inventory = (f.produces(m.good)) ? f.getInventory(m.good) : 0.0;
			}
			buf.append(m.good.id + "-->" + String.valueOf(total_inventory) + " ");
			System.err.println(buf.toString());
		}

	}



	@Override
	public void finish() {
	}

}
