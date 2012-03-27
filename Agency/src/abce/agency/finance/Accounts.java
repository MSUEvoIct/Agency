package abce.agency.finance;


import java.io.*;
import java.util.*;

import sim.engine.*;
import abce.agency.*;
import abce.agency.actions.*;
import abce.agency.firm.*;
import abce.agency.goods.*;
import evoict.reflection.*;



/**
 * 
 * Tracks financial condition and performance for entities representing business
 * firms. Regularizes and consolidates code otherwise shared for both
 * application and network service providers.
 * 
 * @author kkoning
 * 
 */
public class Accounts extends Agent implements Serializable, Steppable {

	private static final long			serialVersionUID		= 1L;

	/**
	 * For how many periods should we track short-term values?
	 * XXX number in source code
	 */
	public static final int				trackingPeriods			= 20;

	// Obvious... the agent for whom this balance sheet is kept.
	private Firm						owner;

	/*
	 * Financial parameters
	 */
	private double						interestRate;
	private double						payoffRate;
	private double						depreciationRate;

	/*
	 * Assets and Liabilities
	 */
	private double						cash					= 0;
	private double						debt					= 0;
	private final Map<Object, Double>	assetAcquisitionCosts	= new HashMap<Object, Double>();
	private final Map<Object, Double>	assetValues				= new HashMap<Object, Double>();
	private double						assetsValue;												// cached
																									// sum
																									// of
																									// above

	/*
	 * Short-term tracking accounts
	 */
	private final double[]				shortInvestment			= new double[trackingPeriods];
	private final double[]				shortInterestPayments	= new double[trackingPeriods];
	private final double[]				shortOperationsCost		= new double[trackingPeriods];
	private final double[]				shortRevenue			= new double[trackingPeriods];
	private final Map<Asset, double[]>	shortAssetRevenues		= new HashMap<Asset, double[]>();
	private final Map<Good, double[]>	shortGoodsRevenue		= new HashMap<Good, double[]>();
	private final Map<Good, double[]>	shortProductionCosts	= new HashMap<Good, double[]>();

	/*
	 * Long-term tracking accounts
	 */
	private double						totalInvestment			= 0;
	private double						totalInterestPayments	= 0;
	private final double				totalOperationsCost		= 0;
	private double						totalRevenue			= 0;
	private final Map<Asset, Double>	totalAssetRevenue		= new HashMap<Asset, Double>();
	private final Map<Good, Double>		totalGoodsRevenue		= new HashMap<Good, Double>();
	private final Map<Good, Double>		totalProductionCosts	= new HashMap<Good, Double>();



	public Accounts() {
	}



	@Override
	public void step(SimState state) {
		super.step(state);
		housekeeping();
	}



	private void housekeeping() {
		// update values of capital assets
		assetsValue = 0;
		for (Object asset : assetValues.keySet()) {
			double oldValue = assetValues.get(asset);
			double newValue = oldValue - oldValue * depreciationRate;
			assetValues.put(asset, newValue);
			assetsValue += newValue;
		}

		// assess interest on outstanding debt
		double interestCharge = debt * interestRate;
		debt += interestCharge;

		// make payment on outstanding debt
		// XXX TODO

		// check for bankruptcy condition
		solvencyCheck();
	}



	private void solvencyCheck() {
		// TODO Auto-generated method stub

	}



	/**
	 * @param capital
	 *            The capital asset/good being capitalized. (required)
	 * @param price
	 *            The aquisition cost of the capital
	 * @return true if the financing is successful, false otherwise.
	 */
	public boolean finance(Object capital, double price) {
		if (capital != null)
			throw new RuntimeException("Cannot capitalize a null asset");

		if (price > getAvailableFinancing())
			return false;

		// We need to pay this back, eventually...
		debt = debt + price;

		// Track the asset
		assetAcquisitionCosts.put(capital, price);
		assetValues.put(capital, price);
		assetsValue = assetsValue + price;

		// Update tracking variables
		shortInvestment[shortIndex()] = shortInvestment[shortIndex()] + price;
		totalInvestment = totalInvestment + price;

		return true;
	}



	/**
	 * Assesses interest on the agent's debt
	 */
	private void assessInterest() {
		double amount = interestRate * debt;
		shortInterestPayments[shortIndex()] += amount;
		totalInterestPayments += amount;
		debt += amount;
	}



	private void depreciate() {
		for (Object asset : assetValues.keySet()) {
			// Calculate amount of depreciation, update asset tracking
			double oldValue = assetValues.get(asset);
			double remainingValueRatio = 1 - depreciationRate;
			double newValue = oldValue * remainingValueRatio;
			assetValues.put(asset, newValue);

			// Update total asset values
			double valueLost = oldValue - newValue;
			assetsValue = assetsValue - valueLost;
		}
	}



	/**
	 * @param capitalAsset
	 *            The capital asset with which this revenue is associated
	 * @param amount
	 *            The amount of revenue
	 */
	public void revenue(Asset capitalAsset, double amount) {
		trackAssetRevenue(capitalAsset, amount);
		revenue(amount);
	}



	public void revenue(Good good, double amount) {
		trackGoodsRevenue(good, amount);
		revenue(amount);
	}



	/**
	 * Spend the specified cost to produce the specified quantity of the
	 * specified goods.
	 * Currently, firms are allowed to borrow if necessary for production.
	 * 
	 * TODO: Track the quantities produced.
	 * 
	 * @param good
	 * @param qty
	 * @param cost
	 */
	public void spend(Good good, double qty, double cost) {
		if (cash > cost) {
			cash -= cost;
		} else {
			// Allow firm to borrow for production
			double remainingCost = cost - cash;
			cash = 0.0;
			borrow(remainingCost);
		}
	}



	public void borrow(double amount) {
		if (amount > getAvailableFinancing())
			throw new RuntimeException("Borrowing " + amount + ", but only "
					+ getAvailableFinancing() + " available.  Shouldn't this amount have been checked first?");
		else
			debt += amount;

	}



	public void revenue(Asset capitalAsset, Good good, double amount) {
		trackAssetRevenue(capitalAsset, amount);
		trackGoodsRevenue(good, amount);
		revenue(amount);
	}



	/**
	 * Earn revenue associated unassociated with an asset or sale of a good.
	 * 
	 * @param amount
	 */
	public void revenue(double amount) {
		// Error checking
		if (Double.isNaN(amount))
			throw new RuntimeException("Cannot earn NaN revenue.");
		if (amount < 0)
			throw new RuntimeException("Cannot earn negative revenue.  Is this an expense?");
		if (Double.isInfinite(amount))
			throw new RuntimeException("Cannot earn infinite revenue.");

		// Increase cash on hand
		cash = cash + amount;

		// Track revenue
		shortRevenue[shortIndex()] = shortRevenue[shortIndex()] + amount;
		totalRevenue = totalRevenue + amount;
	}



	private void trackAssetRevenue(Asset asset, double revenue) {
		if (asset != null) {
			Double oldTotalRevenue = totalAssetRevenue.get(asset);
			if (oldTotalRevenue == null)
				oldTotalRevenue = 0.0;
			double newTotalRevenue = oldTotalRevenue + revenue;
			totalAssetRevenue.put(asset, newTotalRevenue);

			double[] shortRevs = shortAssetRevenues.get(asset);
			if (shortRevs == null)
				shortRevs = new double[trackingPeriods];
			shortRevs[shortIndex()] += revenue;
		} else
			throw new RuntimeException("Trying to track revenue on a null capital asset");

	}



	private void trackGoodsRevenue(Good good, double revenue) {
		if (good != null) {
			Double oldTotalRevenue = totalGoodsRevenue.get(good);
			if (oldTotalRevenue == null)
				oldTotalRevenue = 0.0;
			double newTotalRevenue = oldTotalRevenue + revenue;
			totalGoodsRevenue.put(good, newTotalRevenue);

			double[] shortRevs = shortGoodsRevenue.get(good);
			if (shortRevs == null)
				shortRevs = new double[trackingPeriods];
			shortRevs[shortIndex()] += revenue;
		} else
			throw new RuntimeException("Trying to track revenue on a null good");

	}



	@Stimulus(name = "CapitalAssets")
	public Double getAssetsValue() {
		return assetsValue;
	}



	@Stimulus(name = "LiquidAssets")
	public Double getCashOnHand() {
		return cash;
	}



	@Deprecated
	public void justGiveCashDontUse(double amount) {
		cash += amount;
	}



	/**
	 * Checks to see if the firm has enough resources to support the proposed
	 * production
	 * at the proposed cost.
	 * 
	 * @param productionAction
	 *            the proposed production
	 * @param cost
	 *            the cost thereof
	 * @return true if the firm is financially <i>capable</i> of financing the
	 *         production,
	 *         false otherwise.
	 */
	public boolean verify(ProductionAction productionAction, double cost) {
		double availableFinancing = getAvailableFinancing();
		double cashOnHand = getCashOnHand();
		double maxToSpend;
		if (availableFinancing > cashOnHand)
			maxToSpend = availableFinancing;
		else
			maxToSpend = cashOnHand;

		if (cost <= maxToSpend)
			return true;
		else {
			return false;
		}
	}



	/**
	 * This simple implementation at least provides a debt limit proportional to
	 * the firm's assets.
	 * 
	 * @return the amount this firm can borrow.
	 * 
	 *         XXX Magic numbers/formula, fixme.
	 */
	@Stimulus(name = "AvailableFinancing")
	public double getAvailableFinancing() {
		double totalAssets = getCashOnHand() + getAssetsValue();
		// moving average revenue for the last 5 steps
		double averageShortRevenue = getMovingAverage(shortRevenue, 5);

		// can borrow up to 2x assets + 10x short term revenue
		return 2 * totalAssets + 10 * averageShortRevenue - debt;
	}



	private double getMovingAverage(double[] shortArray, int steps) {
		double total = 0;
		for (int i = 1; i < steps; i++) {
			total = total + shortArray[shortIndex(i)];
		}

		return total / steps;
	}



	@Stimulus(name = "DebtBalance")
	public double getDebtBalance() {
		return debt;
	}



	@Stimulus(name = "InterestRate")
	public double getDebtInterestRate() {
		return interestRate;
	}



	@Stimulus(name = "DebtPayoffRate")
	public double getDebtPayoffRate() {
		return payoffRate;
	}



	@Stimulus(name = "DeltaRevenue")
	public double getDeltaRevenue() {
		double lastPeriodRevenue = shortRevenue[shortIndex(1)];
		double twoPeriodsRevenue = shortRevenue[shortIndex(2)];
		return lastPeriodRevenue - twoPeriodsRevenue;
	}



	@Stimulus(name = "DepreciationRate")
	public double getDepreciationRate() {
		return depreciationRate;
	}



	/**
	 * TODO: Add "goodwill" / future expected revenue
	 * 
	 * @return
	 */
	@Stimulus(name = "NetWorth")
	public double getNetWorth() {
		return assetsValue + cash - debt;
	}



	@Stimulus(name = "LastStepFinancingCost")
	public double getLastStepFinancingCost() {
		return shortInterestPayments[shortIndex(1)];
	}



	@Stimulus(name = "LastStepInvestment")
	public double getLastStepInvestment() {
		return shortInvestment[shortIndex(1)];
	}



	@Stimulus(name = "LastStepOperationsCost")
	public double getLastStepOperationsCost() {
		return shortOperationsCost[shortIndex(1)];
	}



	@Stimulus(name = "LastStepRevenue")
	public double getPerStepRevenue() {
		return shortRevenue[shortIndex(1)];
	}



	@Stimulus(name = "TotalFinancingCost")
	public double getTotalFinancingCost() {
		return totalInterestPayments;
	}



	@Stimulus(name = "TotalInvestment")
	public double getTotalInvestment() {
		return totalInvestment;
	}



	@Stimulus(name = "TotalOperationsCost")
	public double getTotalOperationsCost() {
		return totalOperationsCost;
	}



	@Stimulus(name = "TotalRevenue")
	public double getTotalRevenue() {
		return totalRevenue;
	}



	private void serviceDebt() {
		double interestDue = debt * interestRate;
		double interestPayment = 0;
		double principleDue = debt * payoffRate;
		double principlePayment = 0;
		double totalDue = interestDue + principleDue;

		// Calculate payment totals
		if (totalDue > cash) { // don't have enough
			if (interestDue > cash) { // payments go to interest first
				interestPayment = cash;
				principlePayment = 0;
			} else {
				interestPayment = interestDue;
				principlePayment = cash - interestPayment;
			}
		} else {
			interestPayment = interestDue;
			principlePayment = principleDue;
		}
		double totalPayment = interestPayment + principlePayment;

		// update tracking variables
		shortInterestPayments[shortIndex()] += interestPayment;
		totalInterestPayments += interestPayment;

		// update balances
		debt = debt - totalPayment;
		cash = cash - totalPayment;
	}



	@Override
	public String toString() {
		return "Cash: " + cash + ", Assets: " + assetsValue + ", Debt/Avail: "
				+ debt + "/" + getAvailableFinancing() + ", NetWorth: " + getNetWorth();
	}



	public double getInterestRate() {
		return interestRate;
	}



	public double getPayoffRate() {
		return payoffRate;
	}



	public void setPayoffRate(double payoffRate) {
		this.payoffRate = payoffRate;
	}



	public double getTotalInterestPayments() {
		return totalInterestPayments;
	}



	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}



	public void setDepreciationRate(double depreciationRate) {
		this.depreciationRate = depreciationRate;
	}

}
