package abce.models.io.investment;

import abce.agency.finance.Accounts;

public interface InvestmentFirm {
	
	public double purchaseCapital(InvestmentModel sim, int marketNum);
	public ProductionAction setProduction(InvestmentModel sim, int marketNum);
	public void earnRevenue(double amount);
	
	/**
	 * Verify that the specified action is possible; i.e., that it does
	 * not require more financial resources than the firm has available. 
	 * 
	 * @param capitalPurchaseAction
	 * @return
	 */
	public boolean verify(CapitalPurchaseAction capitalPurchaseAction);
	
	
	/**
	 * The firm must reduce its assets by the amount necessary to accomplish
	 * the production.
	 * 
	 * @param capitalPurchaseAction
	 */
	public void actualize(CapitalPurchaseAction capitalPurchaseAction);

	/**
	 * Verify that the specified action is possible; i.e., that it does
	 * not require more financial resources than the firm has available. 
	 * 
	 * @param productionAction
	 * @return
	 */
	public boolean verify(ProductionAction productionAction);
	
	/**
	 * The firm must reduce its assets by the amount necessary to accomplish
	 * the production.
	 * 
	 * @param productionAction
	 */
	public void actualize(ProductionAction productionAction);
	
}
