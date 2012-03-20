package abce.agency.consumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sim.engine.SimState;
import abce.agency.Agent;
import abce.agency.Market;
import abce.agency.Offer;
import abce.agency.actions.MarketEntry;
import abce.agency.actions.SaleOfGoods;
import abce.agency.goods.Good;

public abstract class Consumer extends Agent {
	private static final long serialVersionUID = 1L;
	
	/*
	 * State Variables
	 */
	private double population;  // how many individuals does this agent represent?

	protected Map<Good,Double> wilingnessToPay = new HashMap<Good,Double>();
	protected Map<Good,Set<Market>> markets = new HashMap<Good,Set<Market>>();
	
	/*
	 * Tracking Variables
	 */
	// Long-term tracking
	private Map<Good,Double> totalQuantityOfGoods = new HashMap<Good,Double>();
	private Map<Good,Double> totalPaidForGoods = new HashMap<Good,Double>();
	private Map<Good,Double> totalConsumerSurplus = new HashMap<Good,Double>();
	
	// Short-term tracking
	private Map<Good,double[]> shortQuantityOfGoods = new HashMap<Good,double[]>();
	private Map<Good,double[]> shortPaidForGoods = new HashMap<Good,double[]>();
	private Map<Good,double[]> shortConsumerSurplus = new HashMap<Good,double[]>();
	
	
	public Consumer(double population) {
		this.population = population;
	}
	
	@Override
	public void step(SimState state) {
		super.step(state);
		// reset short-term quantities
		for ( Good g : shortQuantityOfGoods.keySet()) {
			double[] shortQtyArray = shortQuantityOfGoods.get(g);
			shortQtyArray[shortIndex()] = 0.0;
		}
		for ( Good g : shortPaidForGoods.keySet()) {
			double[] shortPaidForArray = shortPaidForGoods.get(g);
			shortPaidForArray[shortIndex()] = 0.0;
		}
		for ( Good g : shortConsumerSurplus.keySet()) {
			double[] shortSurplusArray = shortConsumerSurplus.get(g);
			shortSurplusArray[shortIndex()] = 0.0;
		}

	}
	

	/**
	 * Find the lowest cost suppliers for <i>all goods</i> for which this
	 * consumer has a positive willingness to pay, one unit for each natural
	 * person represented by this consumer agent (for each and every good), as
	 * long as the price is less than their willingness to pay.
	 */
	protected void findAndConsumeIdeal() {
		findAndConsumeIdeal(this.population);
	}
	
	/**
	 * @return A list of all goods for which this consumer currently has a non-zero
	 * willingness to pay.
	 */
	protected List<Good> allDesiredGoods() {
		List<Good> goods = new ArrayList<Good>();
		for (Good g : this.wilingnessToPay.keySet()) {
			Double wtp = this.wilingnessToPay.get(g);
			if (wtp != null)
				if (wtp > 0.0)
					goods.add(g);
		}	
		return goods;
	}
	
	/**
	 * Find the lowest cost suppliers for <i>all goods</i> for which this
	 * consumer has a positive willingness to pay, up to the specified quantity
	 * (for each and every good), as long as the price is less than their
	 * willingness to pay.
	 */
	protected void findAndConsumeIdeal(double qty) {
		for (Good g : allDesiredGoods()) {
			findAndConsumeIdeal(g, qty);
		}
	}
	
	/**
	 * Find the lowest cost suppliers for the spcified good and consume one unit
	 * from them for each natural person represented by this consumer agent as
	 * long as the price is less than their willingness to pay.
	 * 
	 * @param g
	 */
	protected void findAndConsumeIdeal(Good g) {
		findAndConsumeIdeal(g, this.population);
	}
	
	/**
	 * @param g
	 * @return a list of the available offers for the specified good, in the markets
	 * in which this consumer participates, sorted by lowest price first.
	 */
	protected List<Offer> getSortedOffers(Good g) {
		// Determine the available markets, create set of offers.
		Set<Market> availableMarkets = markets.get(g);
		
		// Find available prices in increasing order (cheapest first)
		List<Offer> offers = new ArrayList<Offer>();
		for (Market m : availableMarkets) {
			offers.addAll(m.getOffers(this, g));
		}
		Collections.sort(offers);		

		return offers;
	}
	
	protected void findAndConsumeIdeal(Good g, double qtyToConsume) {
		// get list or sorted offers
		List<Offer> offers = getSortedOffers(g);
		consumeOrderedOffers(offers,qtyToConsume);
	}
	
	/**
	 * Consume from the specified list of offers, in order, until the specified quantity has
	 * been consumed or the offer price is greater than the consumer's willingness to pay.
	 * 
	 * @param offers An ordered list of offers to purchase from.
	 */
	protected void consumeOrderedOffers(List<Offer> offers, double qtyToConsume) {
		double leftToConsume = qtyToConsume; // initially, we haven't bought anything yet.
		
		// Look at the first offer to determine which good we're looking at
		Offer firstOffer = offers.get(0);
		// Determine the willingness to pay for that good.
		double wtp = wilingnessToPay.get(firstOffer.good);
		
		for (Offer o : offers) {
			if ( leftToConsume <= 0.000001 ) // if we've consumed basically everything,
				break;  // stop looking
			if (o.price <= wtp) {
				/*
				 * There's an offer out there we want to take, figure out how much
				 * to buy and buy it.
				 */
				double qtyFromThisFirm = 0.0;
				if (o.maxQty <= qtyToConsume) // can be satisfied with first offer
					qtyFromThisFirm = o.maxQty;
				else
					qtyFromThisFirm = qtyToConsume;
					
				SaleOfGoods sog = new SaleOfGoods(this, o.firm, o.good, o.price, qtyFromThisFirm);
				sog.process();
				leftToConsume = leftToConsume - sog.quantity;
			}
		}
		
	}
	
	/**
	 * Enter the specified market as a consumer.  The market becomes known to the Consumer
	 * agent through Map Consumer.markets<Good,Set<Market>>
	 * 
	 * @param m
	 */
	public void enterMarket(Market m) {
		MarketEntry me = new MarketEntry(this,m);
		me.process();
	}
	
	/**
	 * Actualize the specified SaleOfGoods transaction.  The default implementation simply
	 * tracks the amount spent, the quantity of goods purchased, and the consumer suplus.
	 * It assumes willingness to pay is constant (i.e., one unit of the good for each natural
	 * person represented by this consumer agent).
	 * 
	 * @param saleOfGoods
	 */
	public void execute(SaleOfGoods saleOfGoods) {
		Good good = saleOfGoods.good;
		double price = saleOfGoods.price;
		double qty = saleOfGoods.quantity;
		double wtp = wilingnessToPay.get(good);
		double totalPaid = price * qty;
		double surplus = (wtp - price ) * qty;
		
		/*
		 * Track short term consumption & surplus
		 */
		double[] shortQtyArray = getShortQtyArray(good);
		double[] shortPaidArray = getShortPaidForGoodsArray(good);
		double[] shortCSArray = getShortConsumerSurplusArray(good);
		shortQtyArray[shortIndex()] += qty;
		shortPaidArray[shortIndex()] += totalPaid;
		shortCSArray[shortIndex()] += surplus;
		
		/*
		 * Track long-term consumption & surplus
		 */
		double totalQty = getTotalQuantityOfGoods(good);
		double totalPrice = getTotalPaidForGood(good);
		double totalCS = getTotalSurplus(good);
		totalQty += qty;
		totalPrice += totalPaid;
		totalCS += surplus;
		totalQuantityOfGoods.put(good,totalQty);
		totalPaidForGoods.put(good, totalPrice);
		totalConsumerSurplus.put(good, totalCS);
	}
	
	/**
	 * Updates Consumer.markets, indicating that the consumer has access to
	 * the specified market.
	 * 
	 * @param marketEntry
	 */
	public void execute(MarketEntry marketEntry) {
		addMarket(marketEntry.market.good,marketEntry.market);
	}
	
	
	/**
	 * @return The number of persons represented by this consumer agent.
	 */
	public double getPopulation() {
		return population;
	}

	/**
	 * @return The total consumer surplus for all goods for all periods.
	 */
	public double getTotalSurplus() {
		double totalSurplus = 0.0;
		for (Good g : totalConsumerSurplus.keySet()) {
			double forThisGood = totalConsumerSurplus.get(g);
			totalSurplus += forThisGood;
		}
		return totalSurplus;
	}
		
	public double getPastQty(Good g, int stepsAgo) {
		if (stepsAgo < 0)
			throw new RuntimeException("Cannot get past qty for a future time");
		if (stepsAgo > trackingPeriods)
			throw new RuntimeException("Only keeping records for " + trackingPeriods + " steps, but "
					+ stepsAgo + " were requested.");
		double[] shortQtyArray = getShortQtyArray(g);
		if (shortQtyArray == null)
			return 0.0;
		return shortQtyArray[shortIndex(stepsAgo)];
	}
	
	public double getPastPaid(Good g, int stepsAgo) {
		if (stepsAgo < 0)
			throw new RuntimeException("Cannot get past anmount paid for a future time");
		if (stepsAgo > trackingPeriods)
			throw new RuntimeException("Only keeping records for " + trackingPeriods + " steps, but "
					+ stepsAgo + " were requested.");
		
		double[] shortPaidArray = getShortPaidForGoodsArray(g);
		if (shortPaidArray == null)
			return 0.0;
		return shortPaidArray[shortIndex(stepsAgo)];
	}
	
	/**
	 * @param g
	 * @param stepsAgo
	 * @return The amount of surplus on the specified good in the specified past 
	 * 		period.
	 */
	public double getPastSurplus(Good g, int stepsAgo) {
		if (stepsAgo < 0)
			throw new RuntimeException("Cannot get past surplus for a future time");
		if (stepsAgo > trackingPeriods)
			throw new RuntimeException("Only keeping records for " + trackingPeriods + " steps, but "
					+ stepsAgo + " were requested.");
		
		double[] shortCSArray = getShortConsumerSurplusArray(g);
		if (shortCSArray == null)
			return 0.0;
		return shortCSArray[shortIndex(stepsAgo)];
	}
	
	/**
	 * @param stepsAgo
	 * @return The amount of surplus in the specified past period for all goods;
	 */
	public double getPastSurplus(int stepsAgo) {
		double forAllGoods = 0.0;
		for (Good g : shortConsumerSurplus.keySet()) {
			forAllGoods += getPastSurplus(g, stepsAgo);
		}
		return forAllGoods;
	}

	/**
	 * Query the amount that persons represented by this consumer agent are willing
	 * to pay for the specified good.  In the default implementation, if no value has
	 * been previously set, this function will return 0.0.
	 * 
	 * @param good Which good to value
	 * @return The amount this consumer agent is willing to pay for the good in question.
	 */
	public double getWTP(Good good) {
		Double wtp = this.wilingnessToPay.get(good);
		if (wtp != null)
			return wtp;
		else
			return 0.0;
	}

	/**
	 * Set the amount that persons represented by this consumer agent are
	 * willing to pay for the specified good.  If a subclass overrides the getWTP()
	 * function (e.g., to dynamically calculate WTP or something...) then this
	 * value may be ignored.
	 * 
	 * @param good
	 * @param price
	 */
	public void setWTP(Good good, double price) {
		this.wilingnessToPay.put(good, price);
	}
	
	public double getTotalQuantityOfGoods(Good g) {
		Double qty = totalQuantityOfGoods.get(g);
		if (qty == null)
			return 0.0;
		else
			return qty;
	}
	
	public double getTotalPaidForGood(Good g) {
		Double total = totalPaidForGoods.get(g);
		if (total == null)
			return 0.0;
		else
			return total;
	}
	
	/**
	 * @param g
	 * @return The total consumer surplus from the specified good.
	 */
	public double getTotalSurplus(Good g) {
		Double surplus = totalConsumerSurplus.get(g);
		if (surplus == null)
			return 0;
		else
			return surplus;
	}
	
	
	/**
	 * When entering new markets, make sure there is a non-null Set<Market> so
	 * that the market can be added.  If there is not yet a non-null WTP for this 
	 * good, initialize it and set it to zero.
	 */
	private void addMarket(Good g, Market m) {
		Set<Market> currentMarkets = markets.get(g);
		if (currentMarkets == null) {  // We haven't entered any markets for this good 
			currentMarkets = new HashSet<Market>();
			markets.put(g, currentMarkets);
		}
		currentMarkets.add(m);
		Double currentWTP = this.wilingnessToPay.get(m.good);
		if (currentWTP == null)
			setWTP(m.good,0.0);
	}
	
	protected double[] getShortQtyArray(Good good) {
		double[] shortQtyArray = shortQuantityOfGoods.get(good);
		if (shortQtyArray == null) {
			shortQtyArray = new double[trackingPeriods];
			shortQuantityOfGoods.put(good,shortQtyArray);
		}
		return shortQtyArray;
	}

	protected double[] getShortPaidForGoodsArray(Good good) {
		double[] shortPaidArray = shortPaidForGoods.get(good);
		if (shortPaidArray == null) {
			shortPaidArray = new double[trackingPeriods];
			this.shortPaidForGoods.put(good,shortPaidArray);
		}
		return shortPaidArray;
	}
	
	protected double[] getShortConsumerSurplusArray(Good good) {
		double[] shortConsumerSurplusArray = shortConsumerSurplus.get(good);
		if (shortConsumerSurplusArray == null) {
			shortConsumerSurplusArray = new double[trackingPeriods];
			shortConsumerSurplus.put(good, shortConsumerSurplusArray);
		}
		return shortConsumerSurplusArray;
	}
	
}
