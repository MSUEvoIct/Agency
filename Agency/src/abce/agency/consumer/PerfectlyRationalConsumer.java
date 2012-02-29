package abce.agency.consumer;

import abce.agency.goods.Good;
import sim.engine.SimState;

/**
 * SimpleConsumer consumers one unit of each good for which it has a specified
 * willingness to pay, assuming the price is less than or equal to the willingness to
 * pay.  If there are multiple firms, it always buys the good from the lowest cost
 * provider with stock.
 * 
 * @author kkoning
 *
 */
public class PerfectlyRationalConsumer extends Consumer {
	private static final long serialVersionUID = 1L;
	
	public PerfectlyRationalConsumer(double population) {
		super(population);
	}
	
	@Override
	public void step(SimState state) {
		super.step(state);
		findAndConsumeIdeal();
	}

}
