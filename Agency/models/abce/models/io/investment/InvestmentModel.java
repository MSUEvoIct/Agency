package abce.models.io.investment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import abce.agency.ec.ecj.AgencyECJSimulation;
import abce.agency.ec.ecj.FitnessListener;
import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

public class InvestmentModel implements AgencyECJSimulation {
	private static final long serialVersionUID = 1L;

	/*
	 * Simulation Parameters
	 */
	// General
	public int numMarkets;
	public int numSteps;

	// Economics Variables
	public double laborExponent;
	public double capitalExponent;
	public double laborCost;
	public double capitalCost;
	public double firmEndowment;

	// Determinants of demand
	public double maximumWTP;
	public double qtyRatio;
	public double qtyExponent;

	/*
	 * Operational Variables
	 */
	public List<InvestmentFirm> firms = new ArrayList<InvestmentFirm>();
	public int step = 0;
	public MersenneTwisterFast random = null;
	List<FitnessListener> fitnessListeners = new ArrayList<FitnessListener>();

	/*
	 * Endogenous economic Variables
	 */
	// Price and Quantity
	public Map<InvestmentFirm, double[][]> qtyProduced = new LinkedHashMap<InvestmentFirm, double[][]>();
	public double[][] marketPrice = null;

	// Accumulated Capital
	public Map<InvestmentFirm, double[][]> capital = new LinkedHashMap<InvestmentFirm, double[][]>();
	public Map<InvestmentFirm, Double> assets = new LinkedHashMap<InvestmentFirm, Double>();

	/*
	 * Simulation Initialization
	 */

	@Override
	public void setup(EvolutionState evoState, Parameter base) {
		ParameterDatabase pb = evoState.parameters;
		this.numMarkets = pb.getInt(base.push("numMarkets"), null);
		this.numSteps = pb.getInt(base.push("numSteps"), null);
		this.laborExponent = pb.getDouble(base.push("laborExponent"), null);
		this.capitalExponent = pb.getDouble(base.push("capitalExponent"), null);
		this.laborCost = pb.getDouble(base.push("laborCost"), null);
		this.capitalCost = pb.getDouble(base.push("capitalCost"), null);
		this.firmEndowment = pb.getDouble(base.push("firmEndowment"), null);

		this.maximumWTP = pb.getDouble(base.push("maximumWTP"), null);
		this.qtyRatio = pb.getDouble(base.push("qtyRatio"), null);
		this.qtyExponent = pb.getDouble(base.push("qtyExponent"), null);
		
		this.marketPrice = new double[numMarkets][numSteps];
	}

	@Override
	public void setSeed(int seed) {
		random = new MersenneTwisterFast(seed);
	}

	@Override
	public void addIndividual(Individual ind) {

		// Individuals required to implement the firm InvestmentFirm interface.
		InvestmentFirm firm = (InvestmentFirm) ind;

		firms.add(firm);
		assets.put(firm, firmEndowment);
		qtyProduced.put(firm, new double[numMarkets][numSteps]);
		capital.put(firm, new double[numMarkets][numSteps]);

	}

	/*
	 * Simulation Operation
	 */

	// Main Loop
	@Override
	public void run() {

		for (int step = 0; step < numSteps; step++) {

			// Run firms
			for (InvestmentFirm firm : firms) {
				for (int marketNum = 0; marketNum < numMarkets; marketNum++) {
					Double firmAssets = this.assets.get(firm);

					double toInvest = firm.purchaseCapital(this, marketNum);
					double toProduce = firm.setProduction(this, marketNum);

					if (toInvest > firmAssets) // budget constraint
						toInvest = firmAssets;

					if (toInvest < 0) // no negative investment
						toInvest = 0.0;

					double capitalPurchased = toInvest / capitalCost;
					setInvestment(firm, marketNum, capitalPurchased);
					firmAssets -= toInvest;
					assets.put(firm, firmAssets);

					double maxProd = maxProduction(firm, marketNum);

					if (toProduce > maxProd) // budget constraint
						toProduce = maxProd;

					if (toProduce < 0) // no negative production
						toProduce = 0.0;

				}
			}

			// Clear all markets
			for (int i = 0; i < numMarkets; i++)
				clearMarket(i);

		}
		
		// All steps have completed
		
		// update FitnessListeners
		for (FitnessListener fl : fitnessListeners) {
			for (InvestmentFirm f : assets.keySet()) {
				
				Individual ind = (Individual) f;
				fl.updateFitness(ind,assets.get(f));
				
			}
		}
		

	}

	public void clearMarket(int marketNum) {
		double totalQty = totalQty(marketNum, step);
		double price = consumerWTP(totalQty);
		marketPrice[marketNum][step] = price;

		for (InvestmentFirm firm : qtyProduced.keySet()) {
			double[][] qtyArray = qtyProduced.get(firm);
			double revenue = qtyArray[marketNum][step] * price;

			double existingAssets = assets.get(firm);
			assets.put(firm, existingAssets + revenue);
		}
	}

	/*
	 * Internal Simulation Information
	 */

	/**
	 * This function determines if the total production quantity for the market
	 * was raised between t-1 and t-2.
	 * 
	 * @param marketNum
	 * @return
	 */
	public boolean qtyIncreased(int marketNum) {
		if (step < 2)
			return false;

		double qtyT_1 = totalQty(marketNum, step - 1);
		double qtyT_2 = totalQty(marketNum, step - 2);

		if (qtyT_1 > qtyT_2)
			return true;
		else
			return false;
	}

	public double productionCost(InvestmentFirm firm, int marketNum, double qty) {
		double[][] capArray = capital.get(firm);
		double capital = capArray[marketNum][step];

		double foo = qty / Math.pow(capital, capitalExponent);
		double labor = Math.pow(foo, 1 / laborExponent);
		return labor * laborCost;
	}

	public double maxProduction(InvestmentFirm firm, int marketNum) {
		double[][] capArray = capital.get(firm);
		double capital = capArray[marketNum][step];

		Double firmAssets = assets.get(firm);
		double maxLabor = firmAssets / laborCost;

		return Math.pow(capital, capitalExponent)
				* Math.pow(maxLabor, laborExponent);
	}

	public double consumerWTP(double qty) {
		double wtp = maximumWTP - Math.pow(qty / qtyRatio, qtyExponent);

		if (wtp < 0.0)
			wtp = 0.0;
		return wtp;
	}

	public void setProduction(InvestmentFirm f, int marketNum, double qty) {
		if (qty < 0) // negative quantities not allowed
			qty = 0.0;

		double[][] qtyArray = qtyProduced.get(f);

		qtyArray[marketNum][step] = qty;
	}

	public void setInvestment(InvestmentFirm f, int marketNum, double qty) {
		if (qty < 0) // negative investment quantities not allowed
			qty = 0.0;

		Double oldQty = 0.0;
		if (step > 0) {
			double[][] capitalArray = capital.get(f);
			oldQty = capitalArray[marketNum][step - 1];
		}

	}

	/**
	 * Gets HHI based on last step's production
	 * 
	 * @param marketNum
	 */
	public Double getHHI(int marketNum) {
		if (step == 0)
			return null;

		double totalProduction = totalQty(marketNum, step - 1);

		double hhi = 0.0;

		for (InvestmentFirm firm : qtyProduced.keySet()) {
			double[][] qtyArray = qtyProduced.get(firm);
			double qtyFirm = qtyArray[marketNum][step - 1];

			double proportion = qtyFirm / totalProduction;
			double percent = proportion * 100;
			hhi += percent * percent;

		}

		return hhi;
	}

	public double totalQty(int marketNum, int stepNum) {
		double totalQty = 0.0;
		for (InvestmentFirm firm : qtyProduced.keySet()) {
			double[][] qtyArray = qtyProduced.get(firm);
			totalQty += qtyArray[marketNum][stepNum];
		}
		return totalQty;
	}

	@Override
	public void addFitnessListener(FitnessListener listener) {
		fitnessListeners.add(listener);
	}

}
