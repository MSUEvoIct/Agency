package abce.models.io.investment;

import abce.agency.firm.Firm;
import sim.engine.SimState;

public class InvestmentModel extends SimState implements Runnable {
	private static final long serialVersionUID = 1L;

	// Simulation parameters
	// TODO:  Matt: externalize these using whatever parameter system is appropriate
	public int numMarkets = 50;
	public int numFirms = 4;
	public int numSteps = 50;

	// Economics Variables
	public double laborExponent = 0.5;
	public double capitalExponent = 0.5;
	public double laborCost = 1;
	public double capitalCost = 1;
	// future
	// public double depreciation = 0.0;
	
	
	/*
	 * Agents
	 */
	public InvestmentFirm[] firms = null;
	
	/*
	 * Agent tracking variables
	 */
	public double[][] revenue = null;
	
	
	
	/*
	 * Simulation Variables
	 */
	// Determinants of demand
	public double[] maximumWTP = null;
	public double[] qtyRatio = null;
	public double[] qtyExponent = null;
	
	
	// Determinants of supply
	public double[][] capital = null;
	public double[][] capitalT1 = null;
	public double[][] capitalT2 = null;
	

	// tracking of production
	public double[][] qtyToProduce = null;
	public double[][] qtyProducedT1 = null;
	public double[][] qtyProducedT2 = null;

	public double totalQty(int marketNum) {
		double totalQty = 0.0;
		for (int i = 0; i < numFirms; i++) {
			totalQty += qtyToProduce[marketNum][i];
		}
		return totalQty;
	}
	
	/**
	 * This function determines if the total production
	 * quantity for the market was raised between t-1 and
	 * t-2.
	 * 
	 * @param marketNum
	 * @return
	 */
	public boolean qtyIncreased(int marketNum) {
		double totalQ1 = 0.0;
		for (int i = 0; i < numFirms; i++)
			totalQ1 += qtyProducedT1[marketNum][i];
		double totalQ2 = 0.0;
		for (int i = 0; i < numFirms; i++)
			totalQ2 += qtyProducedT2[marketNum][i];
		
		if (totalQ1 > totalQ2)
			return true;
		else
			return false;
	}
	
	
	// tracking of prices
	public double[] prices = null;
	public double[] pricesT1 = null;
	public double[] pricesT2 = null;
	
		
	public InvestmentModel(long seed) {
		super(seed);
	}
	
	public void init() {
		firms = new InvestmentFirm[numFirms];
		maximumWTP = new double[numMarkets];
		qtyRatio = new double[numMarkets];
		qtyExponent = new double[numMarkets];
		capital = new double[numMarkets][numFirms];
		capitalT1 = null;
		capitalT2 = null;
		qtyToProduce = new double[numMarkets][numFirms];
		qtyProducedT1 = null;
		qtyProducedT2 = null;

	}
	
	public double productionCost(int marketNum, int firmNum, double qty) {
		double foo = qty / Math.pow(capital[marketNum][firmNum], capitalExponent);
		double labor = Math.pow(foo, 1/laborExponent);
		return labor * laborCost;
	}
	
	public double consumerWTP(int marketNum, double qty) {
		double wtp = maximumWTP[marketNum] - Math.pow(qty/qtyRatio[marketNum], qtyExponent[marketNum]);
		if (wtp < 0.0)
			wtp = 0.0;
		return wtp;
	}
	
	public void setProduction(InvestmentFirm f, int marketNum, double qty) {
		if (qty < 0)  // negative quantities not allowed
			qty = 0.0;
		
		qtyToProduce[marketNum][getFirmNum(f)] = qty;
		
	}
	
	public double capitalCost(double units) {
		return units * capitalCost;
	}
	
	public double laborCost(double units) {
		return units * laborCost;
	}
	
	
	public void clearMarket(int marketNum) {
		double totalQty = totalQty(marketNum);
		double price = consumerWTP(marketNum, totalQty);
		prices[marketNum] = price;
		
		for (int i = 0; i < numFirms; i++) {
			double revenue = qtyToProduce[marketNum][i] * price;
			firms[i].earnRevenue(revenue);
		}
	}
	
	
	@Override
	public void run() {
		// Run all firms
		for (int i = 0; i < numMarkets; i++) {
			for (int j = 0; j < numFirms; j++)
			if (firms[j] != null) {
				firms[j].purchaseCapital(this, i);
				firms[j].setProduction(this, i);
			}
		}
		
		// Clear all markets
		for (int i = 0; i < numMarkets; i++)
			clearMarket(i);
		
		/* 
		 * Move tracking variables
		 */
		// capital
		// reuse capitalT2, copy info from capital
		for (int i = 0; i < numMarkets; i++)
			for (int j = 0; j < numFirms; j++)
				capitalT2[i][j] = capital[i][j];
		Object tmp = capitalT2;
		capitalT2 = capitalT1;
		capitalT1 = capital;
		capital = (double[][]) tmp;
		
		// quantities produced
		// will always be changed, no need to initialize
		tmp = qtyProducedT2;
		qtyProducedT2 = qtyProducedT1;
		qtyProducedT1 = qtyToProduce;
		qtyToProduce = (double[][]) tmp;
		
		// market prices
		// will always be calculated no need to initialize
		tmp = pricesT2;
		pricesT2 = pricesT1;
		pricesT1 = prices;
		prices = (double[]) tmp;
		
		
	}
	
	
	public int getFirmNum(InvestmentFirm firm) {
		for (int i = 0; i < numFirms; i++) {
			if (firms[i].equals(firm)) 
				return i;
		}
		throw new RuntimeException("Firm not in simulation");
	}
	
	/**
	 * Gets HHI based on last step's production
	 * 
	 * @param marketNum
	 */
	public double getHHI(int marketNum) {
		double totalProduction = 0.0;
		for (int i = 0; i < numFirms; i++)
			totalProduction += qtyProducedT1[marketNum][i];
		
		double hhi = 0.0;
		for (int i = 0; i < numFirms; i++) {
			double proportion = qtyProducedT1[marketNum][i] / totalProduction;
			double percent = proportion * 100;
			hhi += percent * percent;
		}
		
		return hhi;
	}
	
	
	
	
	
	
	
}
