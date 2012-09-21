package abce.models.oligopoly.ep;


import abce.agency.Market;
import abce.agency.MarketSimulation;
import abce.agency.firm.Firm;
import abce.util.events.EventProcedureArgs;
import abce.util.BadConfiguration;
import abce.util.events.Procedure;



public class DisplayLast implements Procedure {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;



	@Override
	public void setup(EventProcedureArgs args) throws BadConfiguration {
		// TODO Auto-generated method stub

	}



	@Override
	public void process(Object... context) throws Exception {
		MarketSimulation market = (MarketSimulation) context[0];
		for (Market m : market.getMarkets()) {
			double total_inventory = 0.0;
			for (Firm f : market.getFirms()) {
				System.err.println(f.getProduction(m.good,0) + "@$" + f.getPrice(m.good, null, 0));
			}
		}

	}



	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
