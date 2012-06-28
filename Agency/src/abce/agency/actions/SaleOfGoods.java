package abce.agency.actions;

import abce.agency.Offer;
import abce.agency.consumer.Consumer;

public class SaleOfGoods extends SimulationAction {
	private static final long serialVersionUID = 1L;

	public final Consumer buyer;
	public final Offer offer;
	public final double quantity;
	
	/**
	 * Create a transaction for the sale of goods where the quantity purchased is equal
	 * to the number of persons represented by the consumer agent.
	 * 
	 * @param c The consumer agent buying the good
	 * @param f The firm selling the good
	 * @param g The good
	 * @param price The price of the good
	 */
	public SaleOfGoods(Consumer c, Offer o) {
		this(c,o,c.getPopulation());
	}
	
	/**
	 * Create a transaction for the sale of goods where the quantity purchased is 
	 * explicitly specified.  Keep in mind the fact that the consumer agent represents
	 * an arbitrary nunber of natural persons.
	 * 
	 * @param c The consumer agent buying to good
	 * @param f The firm selling the good
	 * @param g The good
	 * @param quantity The total quantity of the good, spread across all persons
	 * 							represented by the consumer agent
	 * @param price The price of the good
	 */
	public SaleOfGoods(Consumer c, Offer o, double quantity) {
			this.buyer = c;
			this.offer = o;
			this.quantity = quantity;
	}
	
	@Override
	protected String describe() {
		return "Firm " + offer.firm + " selling " + quantity + " of good " + offer.market.good + " to consumer " + buyer + " at price " + offer.price; 
	}

	@Override
	protected void actualize() {
		buyer.actualize(this);
		offer.firm.actualize(this);
		offer.market.actualize(this);
	}

	@Override
	protected boolean isAllowed() {
		return true;  // TODO: How should this be verified?
	}

}
