package abce.agency;

import abce.agency.firm.Firm;
import abce.agency.goods.Good;

public class Offer implements Comparable {
	
	public final Firm firm;
	public final Good good;
	public final double price;
	public final double availQty;

	public Offer(Firm f, Good g, double p, double q) {
		good = g;
		price = p;
		availQty = q;
		firm = f;
	}
	
	@Override
	public int compareTo(Object otherOffer) {
		// TODO Auto-generated method stub
		Offer o = (Offer) otherOffer;
		return Double.compare(price, o.price);
	}
}