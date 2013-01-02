package abce.agency.firm;


import ec.agency.Market;
import ec.agency.actions.ProductionAction;
import ec.agency.actions.SetPriceAction;
import ec.agency.goods.Good;
import sim.engine.SimState;



/**
 * When a SimpleFirm enters a market and produces a good, it will always
 * produces exactly one of each good for each natural person in each market.
 * It charges a fixed, specified price.
 * 
 * @author kkoning
 * 
 */
public class FixedPricingFirm extends Firm implements ProducingPricingFirm {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	public double				price;



	public FixedPricingFirm(double price) {
		this.price = price;
	}



	public void price()
	{
			for (Good g: this.active_goods){
				SetPriceAction pa = new SetPriceAction(this, g, price);
				pa.process();
			}	
	}



	public void produce() {
		/*
		 * Produce as decribed in the object description...
		 */
		
		// TODO: Is iterating over markets necessary?
		for (Market m : this.markets) {
			for (Good g : this.active_goods) {
				double naturalPersons = m.getNumberOfPeople();
				ProductionAction pa = new ProductionAction(this, g, naturalPersons / 20.0);
				pa.process();
			}
		}
	}



	@Override
	public void step(SimState state) {
		produce();
		price();
		super.step(state);
	}

}
