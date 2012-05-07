package abce.agency.firm;


import abce.agency.*;
import abce.agency.actions.*;
import abce.agency.goods.*;



/**
 * When a SimpleFirm enters a market and produces a good, it will always
 * produces exactly one of each good for each natural person in each market.
 * It charges a fixed, specified price.
 * 
 * @author kkoning
 * 
 */
public class SimpleFirm extends Firm {

	public SimpleFirm() {
	}



	@Override
	protected void price() {
		// DO NOTHING. Override getPrice() instead.
		System.err.println("Entering SimpleFirm price()");
	}



	@Override
	protected void produce() {
		/*
		 * Produce as decribed in the object description...
		 */
		for (Market m : this.markets) {
			for (Good g : this.goods) {
				double naturalPersons = m.getNumberOfPeople();
				ProductionAction pa = new ProductionAction(this, g, naturalPersons / 20.0);
				pa.process();
			}
		}
	}

}
