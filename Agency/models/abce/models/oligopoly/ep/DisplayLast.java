package abce.models.oligopoly.ep;


import ec.agency.Market;
import ec.agency.MarketSimulation;
import ec.agency.events.EventProcedureArgs;
import ec.agency.events.Procedure;
import ec.agency.util.BadConfiguration;
import abce.agency.firm.Firm;



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
