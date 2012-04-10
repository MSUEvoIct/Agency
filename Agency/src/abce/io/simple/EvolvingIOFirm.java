package abce.io.simple;

import abce.agency.Market;
import abce.agency.actions.ProductionAction;
import abce.agency.consumer.Consumer;
import abce.agency.firm.Firm;
import abce.agency.firm.SimpleEvolvingFirm;
import abce.agency.goods.Good;
import ec.Problem;
import evoict.reflection.Response;


public class EvolvingIOFirm extends SimpleEvolvingFirm {
	private static final long serialVersionUID = 1L;
	
	private double price;

	/**
	 * As a test case, fix price for now and only worry about production quantity.
	 * 
	 * @param price
	 */
	public EvolvingIOFirm(double price) {
		this.price = price;
	}
	
	/**
	 * Market already contains information about the good, number of consumers, etc...
	 * So this should be a good test case.
	 * 
	 * @author kkoning
	 *
	 */
//	@Stimulus(name = "ProductionStimulus")
	public class ProductionStimulus extends Problem {
		private static final long serialVersionUID = 1L;
		public Market m;
		public Firm f;
		
		public ProductionStimulus(Firm f, Market m) {
			this.m = m;
			this.f = f;
		}
		
		@Response
		void produce(double qty) {
			// Try to do the production of the quantity specified
			ProductionAction pa = new ProductionAction(f,m.good,qty);
			pa.process();
		}
	}
	
	@Override
	protected void price() {
		/*
		 * For now, do nothing... override getPrice instead.  Once we can handle the production stimulus,
		 * add another for pricing.
		 */
		
	}
	
	@Override
	public double getPrice(Good good, Consumer consumer) {
		return price;
	}

	@Override
	protected void produce() {
		/*
		 * Look at every market separately, produce some number (possibly zero) of that good
		 */
		for (Market m : this.markets) {
			ProductionStimulus ps = new ProductionStimulus(this, m);
			this.handleStimulus(ps);
		}
	}

}
