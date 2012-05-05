package abce.agency.actions;

import abce.agency.firm.Firm;
import abce.agency.goods.Good;

public class SetPriceAction extends SimulationAction {

	public final Firm firm;
	public final Good good;
	public final double price;
	
	public SetPriceAction(Firm f, Good g, double price) {
		this.firm = f;
		this.good = g;
		this.price = price;
	}

	/* Allow firms to set any positive, non-infinite price.
	 * 
	 * (non-Javadoc)
	 * @see abce.agency.actions.SimulationAction#verify()
	 */
	@Override
	protected boolean isAllowed() {
		if (price < 0.0)  // reject negative prices
			return false;
		if (Double.isInfinite(price))  // reject infinite prices
			return false;
		if (Double.isNaN(price))  // reject NaN prices
			return false;
		if (firm == null) // can't set a price if there's no firm...
			return false;
		if (good == null) // can't set a price if there's no good...
			return false;
		
		return true; // otherwise, it seems fine.
	}

	@Override
	protected String describe() {
		return "Firm " + firm + " is pricing good " + good + " at " + price;
	}

	@Override
	protected void actualize() {
		firm.actualize(this);
		
	}

}
