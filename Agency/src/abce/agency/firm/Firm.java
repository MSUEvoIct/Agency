package abce.agency.firm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sim.engine.SimState;
import abce.agency.Agent;
import abce.agency.Market;
import abce.agency.Offer;
import abce.agency.actions.MarketEntry;
import abce.agency.actions.ProductionAction;
import abce.agency.actions.SaleOfGoods;
import abce.agency.actions.SetPriceAction;
import abce.agency.async.AsyncUpdate;
import abce.agency.consumer.Consumer;
import abce.agency.finance.Accounts;
import abce.agency.goods.Good;
import abce.agency.production.ProductionFunction;
import evoict.FitnessFunction;
import evoict.NamedFitness;

public abstract class Firm extends Agent implements AsyncUpdate {
	private static final long serialVersionUID = 1L;

	/*
	 * State Variables
	 */

	/**
	 * Describes the financial condition of the firm
	 */
	protected Accounts accounts = new Accounts();

	/**
	 * Quantity in inventory of each good.
	 */
	protected Map<Good, Double> inventory = new HashMap<Good, Double>();

	/**
	 * Contains the production functions necessary to determine a cost of for a
	 * given quantity of production.
	 */
	protected Map<Good, ProductionFunction> productionFunctions = new HashMap<Good, ProductionFunction>();

	/**
	 * A simple map which stores calculated prices for goods produced by this
	 * firm. Derived classes may ignore these values if, e.g., prices are
	 * dynamically calculated.
	 */
	protected Map<Good, Double> prices = new HashMap<Good, Double>();

	/**
	 * The set of markets in which this firm offers its products
	 */
	protected Set<Market> markets = new HashSet<Market>();

	/**
	 * The set of goods produced by this firm.
	 */
	protected Set<Good> goods = new HashSet<Good>();
	
	
	/*
	 * Tracking Variables
	 */
	// Long-term tracking
	private Map<Good,Double> totalQuantityProduced = new HashMap<Good,Double>();
	private Map<Good,Double> totalQuantitySold = new HashMap<Good,Double>();
	private Map<Good,Double> totalRevenue = new HashMap<Good,Double>();
	
	// Short-term tracking
	private Map<Good,double[]> shortQuantityProduced = new HashMap<Good,double[]>();
	private Map<Good,double[]> shortQuantitySold = new HashMap<Good,double[]>();
	private Map<Good,double[]> shortRevenue = new HashMap<Good,double[]>();
	
	
	public Firm() {

	}

	@Override
	public void step(SimState state) {
		super.step(state);
		accounts.step(state);
	}

	@Override
	public void update() {
		spoilage(); // spoilage happens at the tick of every step.

		// Reset short-term quantities
		// -------
		for ( Good g : shortQuantityProduced.keySet()) {
			double[] shortQtyArray = shortQuantityProduced.get(g);
			shortQtyArray[shortIndex()] = 0.0;
		}

		for ( Good g : shortQuantitySold.keySet()) {
			double[] shortQtyArray = shortQuantitySold.get(g);
			shortQtyArray[shortIndex()] = 0.0;
		}

		for ( Good g : shortRevenue.keySet()) {
			double[] shortQtyArray = shortRevenue.get(g);
			shortQtyArray[shortIndex()] = 0.0;
		}

	}

	public void grantEndowment(double amount) {
		accounts.justGiveCashDontUse(amount);
	}

	@NamedFitness(name = "NetWorth")
	protected FitnessFunction netWorth = new FitnessFunction() {

		@Override
		public double getFitness() {
			return accounts.getNetWorth();
		}
	};

	/**
	 * The firm is bankrupt. Clean up!
	 */
	public void bankruptcy() {
		// TODO
	}

	/**
	 * @param good
	 * @return true if the firm (nominally) produces the good, false otherwise.
	 *         Note that whether the firm produces a non-zero amount depends on
	 *         the behavior of the specific firm.
	 */
	public boolean produces(Good good) {
		return goods.contains(good);
	}

	/**
	 * The firm should start producing the specified good. The good will be
	 * listed in the set of goods produced by the firm, but actual (i.e.,
	 * non-zero) production will depend on the exact behavior of the firm.
	 * 
	 * @param good
	 *            The good to start producing
	 * @param pf
	 *            The production function for the good
	 */
	public void startProducing(Good good, ProductionFunction pf) {
		goods.add(good);
		productionFunctions.put(good, pf);

		// Initialize inventory of good.
		Double invQty = inventory.get(good);
		if (invQty == null)
			inventory.put(good, 0.0);
		
		// Initialize tracking data structures for this good.
		// -----------  Long term tracking
		Double totalProduction = totalQuantityProduced.get(good);
		if (totalProduction == null)
			totalQuantityProduced.put(good, 0.0);
		
		Double totalSold = totalQuantitySold.get(good);
		if (totalSold == null)
			totalQuantitySold.put(good, 0.0);
		
		Double revenue = totalRevenue.get(good);
		if (revenue == null)
			totalRevenue.put(good, 0.0);
		
		// ---------  Short Term Tracking
		double[] shortProductionArray = shortQuantityProduced.get(good);
		if (shortProductionArray == null)
			shortQuantityProduced.put(good, new double[trackingPeriods]);
		
		double[] shortQtySold = shortQuantitySold.get(good);
		if (shortQtySold == null)
			shortQuantitySold.put(good, new double[trackingPeriods]);
		
		double[] shortRevenueArray = shortRevenue.get(good);
		if (shortRevenueArray == null)
			shortRevenue.put(good, new double[trackingPeriods]);
		
	}

	/**
	 * The firm will stop producing the specified good. It may still retain an
	 * inventory and may still sell that remaining inventory.
	 * 
	 * @param good
	 *            The good to stop producing
	 */
	public void stopProducing(Good good) {
		goods.remove(good);
	}

	/**
	 * Query the price set by the firm for the specified good to the specified
	 * consumer. The default version just uses the stored value and ignores the
	 * consumer (i.e., does not price discriminate).
	 * 
	 * @param good
	 *            Which good
	 * @param consumer
	 *            Which consumer
	 * @return The price
	 */
	protected double getPrice(Market m, Consumer consumer) {
		Double price = prices.get(m.good);  // XXX Assumes one price for a good in all markets
		if (price != null)
			return price;
		else
			throw new RuntimeException("Firm " + this
					+ " asked to price good, " + m.good
					+ " but has never set a price for it.");
	}

	public final Offer getOffer(Market m, Consumer consumer) {
		if (!this.produces(m.good))
			return null; // this firm does not produce this good; no offer.
		double qtyAvailable = this.getInventory(m);

		// make the offer
		Offer o = new Offer(this, m, getPrice(m, consumer), qtyAvailable);
		return o;
	}

	public double getInventory(Market m) {
		Double invQty = inventory.get(m.good); // XXX Assumes a single inventory across all markets
		if (invQty != null)
			return invQty;
		else
			throw new RuntimeException("Firm " + this
					+ " asked for inventory of good " + m.good
					+ "but has never had one.");
	}

	protected void stock(Good good, Double quantity) {
		// Check for sane input values
		if (quantity == null)
			throw new RuntimeException("Cannot stock a null quantity of good "
					+ good);
		if (quantity.isInfinite())
			throw new RuntimeException(
					"Cannot stock an infinite quantity of good " + good);
		if (quantity.isNaN())
			throw new RuntimeException("Cannot stock a NaN quantity of good "
					+ good);
		if (good == null)
			throw new RuntimeException("Cannot stock a null good");

		// Update quantity
		Double currentQuantity = inventory.get(good);
		if (currentQuantity == null)
			currentQuantity = 0.0;
		double newQuantity = currentQuantity + quantity;
		inventory.put(good, newQuantity);
	}

	protected void deplete(Good good, double quantity) {
		Double existingQuantity = inventory.get(good);
		if (existingQuantity == null)
			throw new RuntimeException("Firm " + this + " doesn't stock good "
					+ good);
		if (existingQuantity < quantity)
			throw new RuntimeException("Firm " + this + " trying to use "
					+ quantity + " units of good " + good + ", only has "
					+ existingQuantity);

		// Update quantity
		double newQuantity = existingQuantity - quantity;
		inventory.put(good, newQuantity);
	}

	protected void spoilage() {
		for (Good good : inventory.keySet()) {
			double currentQty = inventory.get(good);
			double newQty = good.spoil(currentQty);
			inventory.put(good, newQty);
		}
	}

	public void enter(Market m) {
		MarketEntry me = new MarketEntry(this, m);
		me.process();
	}

	/**
	 * Reduces Inventory, receives revenue.
	 * 
	 * @param saleOfGoods
	 */
	public void actualize(SaleOfGoods saleOfGoods) {
		// Decrese Inventory
		deplete(saleOfGoods.offer.market.good, saleOfGoods.quantity);

		// Increase Cash
		accounts.revenue(saleOfGoods.offer.market.good, saleOfGoods.offer.price
				* saleOfGoods.quantity);
		
		// Track long-term sales
		Double totalSales = totalQuantitySold.get(saleOfGoods.offer.market.good);
		totalSales += saleOfGoods.quantity;
		totalQuantitySold.put(saleOfGoods.offer.market.good, totalSales);
		
		// Track short-term sales
		
		
	}

	/**
	 * This firm is now a participant in the specified market.
	 * 
	 * @param marketEntry
	 */
	public void actualize(MarketEntry marketEntry) {
		markets.add(marketEntry.market);
	}

	public ProductionFunction getProductionFunction(Good good) {
		return productionFunctions.get(good);
	}

	public boolean verify(ProductionAction productionAction) {
		ProductionFunction pf = productionFunctions.get(productionAction.good);
		double cost = pf.costOfProducing(productionAction.qty);
		boolean allowed = accounts.verify(productionAction, cost);
		return allowed;
	}

	/**
	 * Add the produced goods to the firm's inventory, pay the required amount
	 * for production.
	 * 
	 * @param productionAction
	 */
	public void actualize(ProductionAction productionAction) {
		ProductionFunction pf = productionFunctions.get(productionAction.good);
		double cost = pf.costOfProducing(productionAction.qty);
		accounts.spend(productionAction.good, productionAction.qty, cost);

		double currentInventory = inventory.get(productionAction.good);
		double newInventory = currentInventory + productionAction.qty;
		inventory.put(productionAction.good, newInventory);
	}

	public void actualize(SetPriceAction setPriceAction) {
		// Simply update the data structure containing the price for this good
		prices.put(setPriceAction.good, setPriceAction.price);
	}
	
	
	/**
	 * @param good
	 * @return The total quantity of this good produced by this firm over the
	 *         entire simulation.
	 */
	public double getTotalQtyProduced(Good good) {
		Double foo = totalQuantityProduced.get(good);
		if (foo == null)
			return 0.0;
		else
			return foo;
	}

	/**
	 * @param good
	 * @return The total quantity of this good sold by this firm over the entire
	 *         simulation.
	 */
	public double getTotalQtySold(Good good) {
		Double foo = totalQuantitySold.get(good);
		if (foo == null)
			return 0.0;
		else
			return foo;
	}

	/**
	 * @param good
	 * @return The total amount of revenue earned by this firm on this product
	 *         over the entire simulation.
	 */
	public double getTotalRevenue(Good good) {
		Double foo = totalRevenue.get(good);
		if (foo == null)
			return 0.0;
		else
			return foo;
	}	
	
	public double getPastQtyProduced(Good g, int stepsAgo) {
		verifyShortData(stepsAgo);
		double[] shortQtyArray = shortQuantityProduced.get(g);
		if (shortQtyArray == null)
			return 0.0;
		return shortQtyArray[shortIndex(stepsAgo)];
	}

	public double getPastQtySold(Good g, int stepsAgo) {
		verifyShortData(stepsAgo);
		double[] shortQtyArray = shortQuantitySold.get(g);
		if (shortQtyArray == null)
			return 0.0;
		return shortQtyArray[shortIndex(stepsAgo)];
	}

	public double getPastRevenue(Good g, int stepsAgo) {
		verifyShortData(stepsAgo);
		double[] shortQtyArray = shortQuantityProduced.get(g);
		if (shortQtyArray == null)
			return 0.0;
		return shortQtyArray[shortIndex(stepsAgo)];
	}



}
