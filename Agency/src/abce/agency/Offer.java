package abce.agency;

import abce.agency.firm.Firm;
import abce.agency.goods.Good;

public class Offer implements Comparable {
	
	public final Firm firm;
	public final Market market;
	public final double price;
	public final double maxQty;
	public final double minQty;

	/**
	 * @param f  The firm offering
	 * @param g The good being offered
	 * @param price The price of the good, per unit.
	 * @param maxQuantity  The maximum quantity available for purchase.
	 */
	public Offer(Firm f, Market m, double price, double maxQuantity) {
		this(f,m,price,maxQuantity,0.0);
	}

	
	/**
	 * Currently private, change if minimum quantities are implemented elsewhere.
	 * 
	 * @param f
	 * @param g
	 * @param price
	 * @param maxQuantity
	 * @param minQuantity
	 */
	private Offer(Firm f, Market m, double price, double maxQuantity, double minQuantity) {
		firm = f;
		market = m;
		this.price = price;
		maxQty = maxQuantity;
		minQty = minQuantity;
	}
	
	@Override
	public int compareTo(Object otherOffer) {
		// TODO Auto-generated method stub
		Offer o = (Offer) otherOffer;
		return Double.compare(price, o.price);
	}
}