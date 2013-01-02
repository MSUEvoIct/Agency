package abce.agency.firm;

import ec.agency.Market;
import ec.agency.actions.ProductionAction;
import ec.agency.goods.Good;

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
