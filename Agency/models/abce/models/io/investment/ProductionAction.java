package abce.models.io.investment;

import abce.agency.actions.SimulationAction;

public class ProductionAction extends SimulationAction {
	private static final long serialVersionUID = 1L;

	private InvestmentModel sim;
	private InvestmentFirm firm;
	private int marketNum;
	private double quantity;

	public ProductionAction(InvestmentModel sim, InvestmentFirm firm,
			int marketNum, double quantity) {
		this.sim = sim;
		this.firm = firm;
		this.marketNum = marketNum;
		this.quantity = quantity;
	}

	@Override
	public String describe() {
		return "Firm #" + sim.getFirmNum(firm) + " producing " + quantity
				+ " in market " + marketNum;
	}

	@Override
	protected boolean isAllowed() {
		return firm.verify(this);
	}

	@Override
	protected void actualize() {
		firm.actualize(this);
		int firmNum = sim.getFirmNum(firm);
		sim.qtyToProduce[marketNum][firmNum] = quantity;
	}

	public double getCost() {
		return sim.productionCost(marketNum, sim.getFirmNum(firm), quantity);
	}

}
