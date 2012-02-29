package abce.agency.firm;

import abce.agency.Market;
import abce.agency.actions.ProductionAction;
import abce.agency.consumer.Consumer;
import abce.agency.goods.Good;

/**
 * When a SimpleFirm enters a market and produces a good, it will always 
 * produces exactly one of each good for each natural person in each market.
 * It charges a fixed, specified price.
 * 
 * @author kkoning
 *
 */
public class SimpleFirm extends Firm {

	private double price;
	
	public SimpleFirm(double price) {
		this.price = price;
	}
	
	@Override
	protected void price() {
		// DO NOTHING.  Override getPrice() instead.
	}
	
	@Override
	public double getPrice(Good good, Consumer consumer) {
		return price;
	}

	@Override
	protected void produce() {
		/*
		 * Produce as decribed in the object description...
		 */
		for (Market m : this.markets) {
			for (Good g : this.goods) {
				double naturalPersons = m.getNumberOfPeople();
				ProductionAction pa = new ProductionAction(this, g, naturalPersons);
				pa.process();
			}
		}
	}

}
