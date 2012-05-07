package abce.agency.actions;


import abce.agency.firm.*;
import abce.agency.goods.*;



public class ProductionAction extends SimulationAction {

	private static final long	serialVersionUID	= 1L;

	public final Firm			firm;
	public final Good			good;
	public final double			qty;



	public ProductionAction(Firm f, Good good, double qty) {
		this.firm = f;
		this.good = good;
		this.qty = qty;
	}



	@Override
	protected String describe() {
		return "Firm " + firm + " producing " + qty + " of good " + good;
	}



	@Override
	public boolean verify() {
		return firm.verify(this);
	}



	@Override
	public void reject() {
		// TODO Auto-generated method stub
		super.reject();
	}



	@Override
	protected void execute() {
		firm.process(this);
	}

}
