package abce.models.io.investment;

import abce.agency.actions.SimulationAction;

public class CapitalPurchaseAction extends SimulationAction {
	private static final long serialVersionUID = 1L;

	private InvestmentModel sim;
	private InvestmentFirm firm;
	private int marketNum;
	private double amount;
	
	public CapitalPurchaseAction(InvestmentModel sim,
			InvestmentFirm firm,
			int marketNum,
			double amount) {
		this.sim = sim;
		this.firm = firm;
		this.marketNum = marketNum;
		this.amount = amount;
	}
	
	
	@Override
	protected String describe() {
		return "Firm #" + sim.getFirmNum(firm) + "investing "
				+ amount + " in market #" + marketNum;
	}

	@Override
	protected boolean isAllowed() {
		return firm.verify(this);
	}

	@Override
	protected void actualize() {
		firm.actualize(this);
	}


	public double getCost() {
		// TODO Auto-generated method stub
		return 0;
	}

}
