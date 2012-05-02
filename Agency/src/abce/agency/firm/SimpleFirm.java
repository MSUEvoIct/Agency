package abce.agency.firm;

import abce.agency.Market;
import abce.agency.actions.ProductionAction;
import abce.agency.goods.Good;
import sim.engine.SimState;

public abstract class SimpleFirm extends Firm {

	/**
	 * Firms must override this method, and perform all pricing activities
	 * within.
	 */
	protected abstract void price();

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

	@Override
	public void step(SimState state) {
		price();
		produce();
		super.step(state);
	}

	
	
}
