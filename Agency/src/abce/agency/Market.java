package abce.agency;


import java.io.*;
import java.util.*;

import abce.agency.actions.*;
import abce.agency.consumer.*;
import abce.agency.firm.*;
import abce.agency.goods.*;
import evoict.reflection.*;



public class Market extends Agent implements Serializable {

	private static final long		serialVersionUID	= 1L;

	private static int				idSequence			= 0;
	public final int				id;

	// Markets are for exactly one good.
	public final Good				good;

	private final Set<Firm>			firms				= new LinkedHashSet<Firm>();
	private final Set<Consumer>		consumers			= new LinkedHashSet<Consumer>();

	// Market tracking data
	/**
	 * The quantity of the good sold by all firms over the entire simulation.
	 */
	protected double				totalQtySold		= 0.0;

	/**
	 * The revenue earned on sales of this good by all firms over the entire
	 * simulation.
	 */
	protected double				totalRevenue		= 0.0;

	/**
	 * The quantity of goods sold by all firms in a single period.
	 */
	protected double[]				shortQtySold		= new double[trackingPeriods];

	/**
	 * The revenue earned on sales of this good by all firms in a single period.
	 */
	protected double[]				shortRevenue		= new double[trackingPeriods];

	/**
	 * The total quanaity of goods sold by a single firm over the entire
	 * simulation.
	 */
	protected Map<Firm, Double>		totalQtySoldByFirm	= new HashMap<Firm, Double>();

	/**
	 * The total revenue earned on sales of this good by a single firm over the
	 * entire simulation.
	 */
	protected Map<Firm, Double>		totalRevenueByFirm	= new HashMap<Firm, Double>();

	/**
	 * The total quantity of goods sold by a single firm in a single period.
	 */
	protected Map<Firm, double[]>	shortQtySoldByFirm	= new HashMap<Firm, double[]>();

	/**
	 * The total revenue earned on sales of this good by a single firm in a
	 * single period.
	 */
	protected Map<Firm, double[]>	shortRevenueByFirm	= new HashMap<Firm, double[]>();



	public Market(Good good) {
		id = idSequence++;

		if (good == null)
			throw new RuntimeException("Cannot have a market for a null good");
		this.good = good;
	}



	public void actualize(SaleOfGoods saleOfGoods) {
		// Just track sales for future information.
		// -----------

		double qty = saleOfGoods.quantity;
		double price = saleOfGoods.offer.price;
		double revenue = qty * price;

		// Quantities sold
		totalQtySold += qty; // total
		shortQtySold[shortIndex()] += qty; // by step

		// by firm
		double firmQty = totalQtySoldByFirm.get(saleOfGoods.offer.firm);
		firmQty += qty;
		totalQtySoldByFirm.put(saleOfGoods.offer.firm, firmQty);

		// by firm and step
		double[] shortQtyArray = shortQtySoldByFirm.get(saleOfGoods.offer.firm);
		shortQtyArray[shortIndex()] += qty;

		// Revenue Earned
		totalRevenue += revenue; // total
		shortRevenue[shortIndex()] += revenue; // by step

		// by firm
		double firmRevenue = totalRevenueByFirm.get(saleOfGoods.offer.firm);
		firmRevenue += revenue;
		totalRevenueByFirm.put(saleOfGoods.offer.firm, firmRevenue);

		// by firm and step
		double[] shortRevArray = shortRevenueByFirm.get(saleOfGoods.offer.firm);
		shortRevArray[shortIndex()] += revenue;

	}



	public Firm[] getFirms() {
		return firms.toArray(new Firm[firms.size()]);
	}



	public Consumer[] getConsumers() {
		return consumers.toArray(new Consumer[consumers.size()]);
	}



	public List<Offer> getOffers(Consumer c) {
		List<Offer> offers = new ArrayList<Offer>();
		for (Firm f : firms) {
			Offer offer = f.getOffer(this, c);
			if (offer != null)
				offers.add(offer);
		}
		return offers;
	}



	/**
	 * The specified consumer is indicating that it will no longer participate
	 * in this market; when market information is gathered, this consumer will
	 * not be included.
	 * 
	 * @param consumer
	 */
	public void exit(Consumer consumer) {
		consumers.remove(consumer);
	}



	@Stimulus(name = "NumFirms")
	public int getNumberOfFirms() {
		return firms.size();
	}



	@Stimulus(name = "NumConsumerAgents")
	public int getNumberOfConsumerAgents() {
		return consumers.size();
	}



	@Stimulus(name = "NumPeople")
	public double getNumberOfPeople() {
		double people = 0.0;
		for (Consumer c : consumers) {
			people += c.population;
		}
		return people;
	}



	public void actualize(MarketEntry marketEntry) {
		if (marketEntry.firm != null) {
			// Track the firm generally
			firms.add(marketEntry.firm);

			// Long term tracking variables
			// --------
			Double foo;
			foo = totalQtySoldByFirm.get(marketEntry.firm);
			if (foo == null)
				totalQtySoldByFirm.put(marketEntry.firm, 0.0);

			foo = totalRevenueByFirm.get(marketEntry.firm);
			if (foo == null)
				totalRevenueByFirm.put(marketEntry.firm, 0.0);

			// Short term tracking variables
			// --------
			double[] bar;
			bar = shortQtySoldByFirm.get(marketEntry.firm);
			if (bar == null)
				shortQtySoldByFirm.put(marketEntry.firm, new double[trackingPeriods]);

			bar = shortRevenueByFirm.get(marketEntry.firm);
			if (bar == null)
				shortRevenueByFirm.put(marketEntry.firm, new double[trackingPeriods]);
		}

		if (marketEntry.consumer != null)
			consumers.add(marketEntry.consumer);

	}



	// Sales Information
	// ---------------------

	/**
	 * @return The total quantity of the good sold by all firms over the entire
	 *         simulation.
	 */
	public double getTotalQtySold() {
		return totalQtySold;
	}



	/**
	 * @return The total revenue earned by all firms from sales in this market
	 *         over the entire simulation.
	 */
	public double getTotalRevenue() {
		return totalRevenue;
	}



	/**
	 * @param stepsAgo
	 * @return The total quantity of the good sold by all firms in a previous
	 *         step.
	 */
	public double getShortQtySold(int stepsAgo) {
		verifyShortData(stepsAgo);
		double shortQty = shortQtySold[shortIndex()];
		return shortQty;
	}



	/**
	 * @param stepsAgo
	 * @return The total revenue earned from goods sold in this market by all
	 *         firms in a previous time step.
	 */
	public double getShortRevenue(int stepsAgo) {
		verifyShortData(stepsAgo);
		double foo = shortRevenue[shortIndex()];
		return foo;
	}



	/**
	 * @param f
	 * @return The total quantity of goods sold by a firm in this market over
	 *         the entire simulation.
	 */
	public double getTotalQtySold(Firm f) {
		Double firmTotal = totalQtySoldByFirm.get(f);
		if (firmTotal == null)
			return 0.0;
		else
			return firmTotal;
	}



	/**
	 * @param f
	 * @return The total revenue earned by a single firm from goods sold in this
	 *         market over the entire simulation.
	 */
	public double getTotalRevenue(Firm f) {
		Double firmTotal = totalRevenueByFirm.get(f);
		if (firmTotal == null)
			return 0.0;
		else
			return firmTotal;
	}



	/**
	 * @param f
	 * @param stepsAgo
	 * @return The quantity of goods sold by a firm in this market in a previous
	 *         step.
	 */
	public double getShortQtySold(Firm f, int stepsAgo) {
		double[] foo = shortQtySoldByFirm.get(f);
		double qty = foo[shortIndex()];
		return qty;
	}



	/**
	 * @param f
	 * @param stepsAgo
	 * @return The revenue earned by a single firm from goods sold in this
	 *         market in a previous step.
	 */
	public double getShortRevenue(Firm f, int stepsAgo) {
		double[] foo = shortRevenueByFirm.get(f);
		double qty = foo[shortIndex()];
		return qty;
	}



	// Derived Market Information
	// ---------

	@Stimulus(name = "AvgMarketPrice")
	public double avgMarketPrice() {
		double qty = getShortQtySold(1); // quantity sold by all firms from one
											// step ago
		double revenue = getShortRevenue(1); // revenue earned by all firms one
												// step ago

		if (revenue == 0.0) // prevent division by zero
			revenue = Double.MIN_VALUE;

		double avgPrice = qty / revenue;

		return avgPrice;
	}



	@Stimulus(name = "Sales(T-1)")
	public double lastPeriodSales() {
		return getShortQtySold(1);
	}

}
