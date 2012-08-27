package abce.agency.firm;

import abce.agency.Market;
import abce.agency.actions.ProductionAction;
import abce.agency.goods.Good;

public class FixedProductionPricingFirm extends FixedPricingFirm {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public double production;
	
	public FixedProductionPricingFirm(double price, double production){
		super(price);
		this.production = production;
	}
	
	public void produce(){
			for (Good g: this.active_goods){
				ProductionAction pa = new ProductionAction(this, g, production);
				pa.process();
			}
	}

}
