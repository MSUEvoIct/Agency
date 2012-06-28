package abce.agency.events;


import abce.agency.Market;
import abce.agency.MarketSimulation;
import abce.agency.firm.Firm;
import abce.agency.util.BadConfiguration;



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
				System.err.println(f.getLastProduction(m.good) + "@$" + f.getPrice(m, null));
			}
		}

	}



	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
