package abce.models.io.investment;

import abce.agency.ec.StimulusResponse;
import abce.agency.finance.Accounts;
import abce.util.reflection.MethodDictionary;

public class BasicInvestmentFirm implements InvestmentFirm {

	private Accounts accounts;
	
	public class CapitalPurchase implements StimulusResponse {

		@Override
		public MethodDictionary dictionary() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	
	@Override
	public double purchaseCapital(InvestmentModel sim, int marketNum) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public ProductionAction setProduction(InvestmentModel sim, int marketNum) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	public void earnRevenue(double amount) {
		accounts.revenue(amount);
	}

	@Override
	public boolean verify(CapitalPurchaseAction capitalPurchaseAction) {
		double spendingLimit = accounts.getAvailableFinancing();
		double cost = capitalPurchaseAction.getCost();
		if (spendingLimit < cost)
			return false;
		else
			return true;
	}

	@Override
	public void actualize(CapitalPurchaseAction capitalPurchaseAction) {
		double cost = capitalPurchaseAction.getCost();
		accounts.spend(cost);
	}

	@Override
	public boolean verify(ProductionAction productionAction) {
		double spendingLimit = accounts.getAvailableFinancing();
		double cost = productionAction.getCost();
		if (spendingLimit < cost)
			return false;
		else
			return true;
	}

	@Override
	public void actualize(ProductionAction productionAction) {
		double cost = productionAction.getCost();
		accounts.spend(cost);
	}

}
