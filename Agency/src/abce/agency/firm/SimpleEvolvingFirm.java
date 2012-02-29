package abce.agency.firm;

import abce.agency.consumer.Consumer;
import abce.agency.goods.Good;

/**
 * SimpleEvolvingFirm should contain basic environment probes and actions
 * that apply to all firms.
 * 
 * 
 * @author kkoning
 *
 */
public abstract class SimpleEvolvingFirm extends Firm {

	@Override
	protected void price() {
		/*
		 * Calculate a price for every good we produce.
		 */
		// TODO Auto-generated method stub

	}

	@Override
	protected void produce() {
		// TODO Auto-generated method stub

	}

	/*
	 * 
	 * 
	 * (non-Javadoc)
	 * @see abce.agency.firm.Firm#getPrice(abce.agency.Good, abce.agency.Consumer)
	 */
	@Override
	public double getPrice(Good good, Consumer consumer) {
		// TODO Auto-generated method stub
		return super.getPrice(good, consumer);
	}

	/* 
	 * 
	 */
	@Override
	public double getInventory(Good good) {
		// TODO Auto-generated method stub
		return super.getInventory(good);
	}
	
	

}
