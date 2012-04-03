package abce.agency.firm;


import java.util.*;

import sim.engine.*;
import abce.agency.*;
import abce.agency.actions.*;
import abce.agency.async.*;
import abce.agency.consumer.*;
import abce.agency.finance.*;
import abce.agency.goods.*;
import abce.agency.production.*;
import evoict.*;



public abstract class Firm extends Agent implements AsyncUpdate {

	private static final long				serialVersionUID	= 1L;

	/*
	 * State Variables
	 */

	/**
	 * Describes the financial condition of the firm
	 */
	protected Accounts						accounts			= new Accounts();

	protected Map<Good, Double>				inventory			= new HashMap<Good, Double>();

	/**
	 * Contains the production functions necessary to determine a cost of for
	 * a given quantity of production.
	 */
	protected Map<Good, ProductionFunction>	productionFunctions	= new HashMap<Good, ProductionFunction>();

	/**
	 * A simple map which stores calculated prices for goods produced by this
	 * firm.
	 * Derived classes may ignore these values if, e.g., prices are dynamically
	 * calculated.
	 */
	protected Map<Good, Double>				prices				= new HashMap<Good, Double>();

	/**
	 * The set of markets in which this firm offers its products
	 */
	protected Set<Market>					markets				= new HashSet<Market>();

	/**
	 * The set of goods produced by this firm.
	 */
	protected Set<Good>						goods				= new HashSet<Good>();



	public Firm() {
	}



	@Override
	public void step(SimState state) {
		System.err.println("Stepping");
		super.step(state);
		produce();
		price();
		accounts.step(state);
	}



	@Override
	public void update() {
		spoilage(); // spoilage happens at the tick of every step.
	}



	public void grantEndowment(double amount) {
		accounts.justGiveCashDontUse(amount);
	}



	/**
	 * Firms must override this method, and perform all pricing activities
	 * within.
	 */
	protected abstract void price();



	/**
	 * Firms must override this method, and perform all production activities
	 * within.
	 */
	protected abstract void produce();

	@NamedFitness(name = "NetWorth")
	protected FitnessFunction	netWorth	= new FitnessFunction() {

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
	 *         Note
	 *         that whether the firm produces a non-zero amount depends on the
	 *         behavior of
	 *         the specific firm.
	 */
	public boolean produces(Good good) {
		return goods.contains(good);
	}



	/**
	 * The firm should start producing the specified good. The good will be
	 * listed in the
	 * set of goods produced by the firm, but actual (i.e., non-zero) production
	 * will depend
	 * on the exact behavior of the firm.
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
	}



	/**
	 * The firm will stop producing the specified good. It may still retain an
	 * inventory
	 * and may still sell that remaining inventory.
	 * 
	 * @param good
	 *            The good to stop producing
	 */
	public void stopProducing(Good good) {
		goods.remove(good);
	}



	/**
	 * Query the price set by the firm for the specified good to the specified
	 * consumer.
	 * The default version just uses the stored value and ignores the consumer
	 * (i.e.,
	 * does not price discriminate).
	 * 
	 * @param good
	 *            Which good
	 * @param consumer
	 *            Which consumer
	 * @return The price
	 */
	protected double getPrice(Good good, Consumer consumer) {
		Double price = prices.get(good);
		if (price != null)
			return price;
		else
			throw new RuntimeException("Firm " + this + " asked to price good, " + good
					+ " but has never set a price for it.");
	}



	public final Offer getOffer(Good good, Consumer consumer) {
		if (!this.produces(good))
			return null; // this firm does not produce this good; no offer.
		double qtyAvailable = this.getInventory(good);
		if (qtyAvailable <= 0.0)
			return null; // the firm has no inventory

		// make the offer
		Offer o = new Offer(this, good, getPrice(good, consumer), qtyAvailable);
		return o;
	}



	public double getInventory(Good good) {
		Double invQty = inventory.get(good);
		if (invQty != null)
			return invQty;
		else
			throw new RuntimeException("Firm " + this + " asked for inventory of good " + good
					+ "but has never had one.");
	}



	protected void stock(Good good, Double quantity) {
		// Check for sane input values
		if (quantity == null)
			throw new RuntimeException("Cannot stock a null quantity of good " + good);
		if (quantity.isInfinite())
			throw new RuntimeException("Cannot stock an infinite quantity of good " + good);
		if (quantity.isNaN())
			throw new RuntimeException("Cannot stock a NaN quantity of good " + good);
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
			throw new RuntimeException("Firm " + this + " doesn't stock good " + good);
		if (existingQuantity < quantity)
			throw new RuntimeException("Firm " + this + " trying to use " + quantity +
					" units of good " + good + ", only has " + existingQuantity);

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
	public void execute(SaleOfGoods saleOfGoods) {
		accounts.revenue(saleOfGoods.good, saleOfGoods.price * saleOfGoods.quantity);
		deplete(saleOfGoods.good, saleOfGoods.quantity);
	}



	/**
	 * This firm is now a participant in the specified market.
	 * 
	 * @param marketEntry
	 */
	public void execute(MarketEntry marketEntry) {
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
	public void process(ProductionAction productionAction) {
		ProductionFunction pf = productionFunctions.get(productionAction.good);
		double cost = pf.costOfProducing(productionAction.qty);
		accounts.spend(productionAction.good, productionAction.qty, cost);

		double currentInventory = inventory.get(productionAction.good);
		double newInventory = currentInventory + productionAction.qty;
		inventory.put(productionAction.good, newInventory);
	}

}
