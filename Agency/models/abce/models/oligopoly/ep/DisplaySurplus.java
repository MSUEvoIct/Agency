package abce.models.oligopoly.ep;


import abce.agency.Market;
import abce.agency.MarketSimulation;
import abce.agency.firm.Firm;
import abce.util.events.EventProcedureArgs;
import abce.util.events.Procedure;
import abce.util.BadConfiguration;



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
				total_inventory = (f.hasProduced(m.good)) ? f.getInventory(m.good, 1) : 0.0;
			}
			buf.append(m.good.id + "-->" + String.valueOf(total_inventory) + " ");
			System.err.println(buf.toString());
		}

	}



	@Override
	public void finish() {
	}

}
