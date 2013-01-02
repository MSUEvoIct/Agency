package abce.agency.firm;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import ec.agency.Agent;
import ec.agency.Market;
import ec.agency.Offer;
import ec.agency.actions.MarketEntry;
import ec.agency.actions.ProductionAction;
import ec.agency.actions.SaleOfGoods;
import ec.agency.actions.SetPriceAction;
import ec.agency.async.AsyncUpdate;
import ec.agency.consumer.Consumer;
import ec.agency.goods.Good;
import ec.agency.production.ProductionFunction;
import ec.agency.util.FitnessFunction;
import ec.agency.util.NamedFitness;

import sim.engine.SimState;
import abce.agency.finance.Accounts;



public abstract class Firm extends Agent implements AsyncUpdate {

	private static final long				serialVersionUID		= 1L;

	/*
	 * State Variables
	 */

	/**
	 * Describes the financial condition of the firm
	 */
	protected Accounts						accounts				= new Accounts(this);

	/**
	 * Contains the production functions necessary to determine a cost of for
	 * a given quantity of production.
	 */
	protected Map<Good, ProductionFunction>	productionFunctions		= new LinkedHashMap<Good, ProductionFunction>();


	/**
	 * The set of markets in which this firm offers its products
	 */
	protected Set<Market>					markets					= new LinkedHashSet<Market>();

	/**
	 * The set of goods produced by this firm.
	 * total: goods that were ever produced
	 * active: goods that are currently produced
	 */
	protected Set<Good>					total_goods					= new LinkedHashSet<Good>();
	protected Set<Good>						active_goods			= new LinkedHashSet<Good>();
	/*
	 * Tracking Variables
	 */
	// Long-term tracking
	private final Map<Good, Double>			totalQuantityProduced	= new HashMap<Good, Double>();
	private final Map<Good, Double>			totalQuantitySold		= new HashMap<Good, Double>();
	private final Map<Good, Double>			totalRevenue			= new HashMap<Good, Double>();

	// Short-term tracking
	private final Map<Good, double[]>		shortPrice				= new HashMap<Good, double[]>();
	private final Map<Good, double[]>		shortQuantityProduced	= new HashMap<Good, double[]>();
	private final Map<Good, double[]>		shortInventory			= new HashMap<Good, double[]>();
	private final Map<Good, double[]>		shortQuantitySold		= new HashMap<Good, double[]>();
	private final Map<Good, double[]>		shortRevenue			= new HashMap<Good, double[]>();



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
		for (Good g: active_goods){
			setCurrent(shortPrice.get(g), getHistoric(shortPrice.get(g),1));
			setCurrent(shortQuantityProduced.get(g), 0.0);
			setCurrent(shortInventory.get(g), getHistoric(shortInventory.get(g),1));
			setCurrent(shortQuantitySold.get(g), 0.0);
			setCurrent(shortRevenue.get(g), 0.0);
		}
	}



	public void grantEndowment(double amount) {
		accounts.justGiveCashDontUse(amount);
	}
	

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
	 *         Note that whether the firm produces a non-zero amount depends on
	 *         the behavior of the specific firm.
	 */
	public boolean hasProduced(Good good) {
		return total_goods.contains(good);
	}
	
	
	/**
	 * @param good
	 * @return true if the firm currently produces the good, false otherwise.
	 */
	public boolean doesProduce(Good good){
		return active_goods.contains(good);
	}



	/**
	 * @return
	 *         The accounts associated with this firm.
	 */
	public Accounts getAccounts() {
		return accounts;
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
		active_goods.add(good);
		total_goods.add(good);
		productionFunctions.put(good, pf);
		



		// Initialize tracking data structures for this good.
		// ----------- Long term tracking
		if (!totalQuantityProduced.containsKey(good))
			totalQuantityProduced.put(good, 0.0);
		if (!totalQuantitySold.containsKey(good))
			totalQuantitySold.put(good, 0.0);
		if (!totalRevenue.containsKey(good))
			totalRevenue.put(good, 0.0);

		// --------- Short Term Tracking
		if (!shortPrice.containsKey(good)){
			shortPrice.put(good, new double[trackingPeriods]);
			setHistoric(shortPrice.get(good), 1, 0.0);
		}
		if (!shortInventory.containsKey(good)){
			shortInventory.put(good, new double[trackingPeriods]);
			setHistoric(shortInventory.get(good), 1, 0.0);
		}
		if (!shortQuantityProduced.containsKey(good)){
			shortQuantityProduced.put(good, new double[trackingPeriods]);
			setHistoric(shortQuantityProduced.get(good), 1, 0.0);
		}
		if (!shortQuantitySold.containsKey(good)){
			shortQuantitySold.put(good, new double[trackingPeriods]);
			setHistoric(shortQuantitySold.get(good), 1, 0.0);
		}
		if (!shortRevenue.containsKey(good)){
			shortRevenue.put(good,  new double[trackingPeriods]);
			setHistoric(shortRevenue.get(good), 1, 0.0);
		}
	
	}


	/**
	 * Set the last production.  This is used usually at initialization time.
	 * @param good
	 * @param value
	 */
	public void setInitialProduction(Good good, double value){
		setHistoric(shortQuantityProduced.get(good), 1, value);
	}
	
	/**
	 * Set the last price for a good.  This is usually used at initialization time.
	 * @param good
	 * 	Good to set the last price of
	 * @param value
	 * 	The value of the good
	 */
	public void setInitialPrice(Good good, double value){
		setHistoric(shortPrice.get(good), 1, value);
	}
	

	/**
	 * The firm will stop producing the specified good. It may still retain an
	 * inventory and may still sell that remaining inventory.
	 * 
	 * @param good
	 *            The good to stop producing
	 */
	public void stopProducing(Good good) {
		active_goods.remove(good);
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
	 * @param back
	 * 			  How many steps back to go
	 * @return The price
	 */
	public double getPrice(Good g, Consumer consumer, int back) {
		if (shortPrice.containsKey(g)){
			return getHistoric(shortPrice.get(g), back);
		}else{
			throw new RuntimeException("Firm " + this
					+ " asked to price good, " + g
					+ " but has never set a price for it.");
		}
	}

	/**
	 * Query the production set by the firm for the specified good.
	 * 
	 * @param good
	 *            Which good
	 * @param back
	 * 			  How many steps back to go
	 * @return The price
	 */
	public double getProduction(Good g, int back) {
		if (shortQuantityProduced.containsKey(g)){
			return getHistoric(shortQuantityProduced.get(g),back);
		} else {
			throw new RuntimeException("Firm " + this
					+ "asked to get the production of"
					+ " good " + g + " but has never set a "
					+ "production level for it");
		} 
	}

	
	/**
	 * Query the inventory of a good
	 * 
	 * @param good
	 *            Which good
	 * @param back
	 * 			  How many steps back to go
	 * @return The price
	 */
	public double getInventory(Good g, int back){
		if (shortInventory.containsKey(g)){
			return getHistoric(shortInventory.get(g), back);
		} else {
			throw new RuntimeException("Firm " + this +
					" asked to get the inventory of good " + g +
					" but has no record of it");
		}
	}
	
	
	/**
	 * Query the quantity sold of a good
	 * 
	 * @param good
	 *            Which good
	 * @param back
	 * 			  How many steps back to go
	 * @return The price
	 */
	public double getSales(Good g, int back){
		if (shortQuantitySold.containsKey(g)){
			return getHistoric(shortQuantitySold.get(g), back);
		} else {
			throw new RuntimeException("Firm " + this + 
					" asked to get the sales of good " + g + 
					" but has no record of it");
		}
	}
	
	
	/**
	 * Query the revenue gotten for the sale of a good
	 * 
	 * @param good
	 *            Which good
	 * @param back
	 * 			  How many steps back to go
	 * @return The price
	 */
	public double getRevenue(Good g, int back){
		if (shortRevenue.containsKey(g)){
			return getHistoric(shortRevenue.get(g), back);
		} else {
			throw new RuntimeException("Firm " + this + 
					" asked to get the revune of good " + g+
					" but has no record of it");
		}
	}


	/**
	 * Sets the current price for a particular good
	 * 
	 * @param good
	 * @param price
	 */
	public void setPrice(Good good, double price) {
		if (shortPrice.containsKey(good))
			setCurrent(shortPrice.get(good), price);
		else
			throw new RuntimeException("Firm " + this + "tried to set"
					+ " the price of good " + good + ", but this good is "
					+ "unknown to the firm.");
	}


	/**
	 * Sets the current production quantity for a particular good
	 * 
	 * @param good
	 * @param price
	 */
	public void setProduced(Good good, double qty){
		if (shortQuantityProduced.containsKey(good)){
			setCurrent(shortQuantityProduced.get(good), qty);
		} else {
			throw new RuntimeException("Firm " + this + " tried to set " 
					+ "the production quantity of good " + good + " but"
					+ "the good is unknown to the firm.");
		}
	}
	
	
	/**
	 * Sets the current inventory for a particular good
	 * 
	 * @param good
	 * @param price
	 */
	public void setInventory(Good good, double qty){
		if (shortInventory.containsKey(good)){
			setCurrent(shortInventory.get(good), qty);
		} else {
			throw new RuntimeException("Firm " + this + " tried to set " 
					+ "the inventory of good " + good + " but"
					+ "the good is unknown to the firm.");
		}
	}
	
	
	/**
	 * Sets the current quantity sold for a particular good
	 * 
	 * @param good
	 * @param price
	 */
	public void setSales(Good good, double qty){
		if (shortQuantitySold.containsKey(good)){
			setCurrent(shortQuantitySold.get(good), qty);
		} else {
			throw new RuntimeException("Firm " + this + " tried to set " 
					+ "the quantity sold of good " + good + " but"
					+ "the good is unknown to the firm.");
		}
	}
	
	
	/**
	 * Sets the current revenue for a particular good
	 * 
	 * @param good
	 * @param price
	 */
	public void setRevenue(Good good, double qty){
		if (shortRevenue.containsKey(good)){
			setCurrent(shortRevenue.get(good), qty);
		} else {
			throw new RuntimeException("Firm " + this + " tried to set " 
					+ "the revenue earned for good " + good + " but"
					+ "the good is unknown to the firm.");
		}
	}
	
	
	

	public final Offer getOffer(Market m, Consumer consumer) {
		Good g = m.good;
		if (!this.hasProduced(g))
			return null; // this firm does not produce this good; no offer.
		double qtyAvailable = this.getInventory(g,0);

		// make the offer
		Offer o = new Offer(this, m, getPrice(g, consumer, 0), qtyAvailable);
		return o;
	}




	protected void stock(Good good, Double quantity) {
		// Check for sane input values
		if (quantity == null)
			throw new RuntimeException("Cannot stock a null quantity of good "
					+ good);
		if (!quantity.isInfinite() || quantity.isNaN())
			throw new RuntimeException(
					"Cannot stock an non-finite quantity of good " + good);
		if (good == null)
			throw new RuntimeException("Cannot stock a null good");

		// Update quantity
		Double currentQuantity = getInventory(good, 0);
		double newQuantity = currentQuantity + quantity;
		setInventory(good, newQuantity);
	}



	protected void deplete(Good good, double quantity) {
		Double existingQuantity = getInventory(good, 0);
		if (existingQuantity == null)
			throw new RuntimeException("Firm " + this + " doesn't stock good "
					+ good);
		if (existingQuantity < quantity)
			throw new RuntimeException("Firm " + this + " trying to use "
					+ quantity + " units of good " + good + ", only has "
					+ existingQuantity);

		// Update quantity
		double newQuantity = existingQuantity - quantity;
		setInventory(good, newQuantity);
	}



	protected void spoilage() {
		for (Good good : shortInventory.keySet()) {
			double currentQty = getInventory(good,0);
			double newQty = good.spoil(currentQty);
			setInventory(good, newQty);
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
		Good g = saleOfGoods.offer.market.good;
		double qty = saleOfGoods.quantity;
		double price = saleOfGoods.offer.price;
		
		// Decrese Inventory
		deplete(g, qty);

		// Increase Cash
		accounts.revenue(g, price * qty);

		// Track long-term sales
		totalQuantitySold.put(g, totalQuantitySold.get(g) + qty);

		// Track short-term sales
		setSales(g, getSales(g,0) + qty);
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
		boolean allowed = productionAction.qty >= 0;
		double cost = pf.costOfProducing(productionAction.qty);
		allowed = allowed && accounts.verify(productionAction, cost);
		return allowed;
	}



	/**
	 * Add the produced goods to the firm's inventory, pay the required amount
	 * for production.
	 * 
	 * @param productionAction
	 */
	public void actualize(ProductionAction productionAction) {
		Good g = productionAction.good;
		double qty = productionAction.qty;
		ProductionFunction pf = productionFunctions.get(g);
		double cost = pf.costOfProducing(qty);
		accounts.spend(g, qty, cost);

		setInventory(g, getInventory(g,0) + qty);
		setProduced(g, getProduction(g,0) + qty);
	}



	/**
	 * Set the price for a particular good
	 * @param setPriceAction
	 */
	public void actualize(SetPriceAction setPriceAction) {
		setPrice(setPriceAction.good, setPriceAction.price);
	}

/*

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

	
	/**
	 * Return the current value of a short-term tracking array
	 * @param short_data
	 * 	data array
	 * @return
	 */
	protected double getCurrent(double[] short_data){
		if (short_data == null){
			throw new RuntimeException("Unable to retrieve current data; array is null");
		} else {
			return short_data[shortIndex()];
		}
	}
	
	
	
	/**
	 * Return a historic value from a short-term tracking array
	 * @param short_data
	 * 	data array
	 * @param back
	 * 	periods back
	 * @return
	 */
	protected double getHistoric(double[] short_data, int back){
		if (short_data == null){
			throw new RuntimeException("Unable to retrieve historic data " + back + " periods back; array is null");
		} else {
			return short_data[shortIndex(back)];
		}
	}
	
	/**
	 * Set the current index into a tracking variable to a particular value
	 * @param short_data
	 * 	tracking array
	 * @param value
	 * 	new value
	 */
	protected void setCurrent(double[] short_data, double value){
		if (short_data == null){
			throw new RuntimeException("Unable to set the current value of a short term element because the data array is null.");
		} else {
			short_data[shortIndex()] = value;
		}
	}
	
	/**
	 * Set the a historic index into a tracking variable to a particular value
	 * @param short_data
	 * 	tracking array
	 * @param back
	 * 	number of periods back
	 * @param value
	 * 	new value
	 */
	protected void setHistoric(double[] short_data, int back, double value){
		if (short_data == null){
			throw new RuntimeException("Unable to set " + back + " steps back of data; the array points to null");
		} else {
			short_data[shortIndex(back)] = value;
		}
	}
	

}
