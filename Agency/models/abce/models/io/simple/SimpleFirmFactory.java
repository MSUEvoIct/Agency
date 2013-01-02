package abce.models.io.simple;

import ec.agency.goods.Good;
import ec.agency.production.ProductionFunction;
import abce.agency.firm.Firm;
import abce.agency.firm.FirmFactory;

/**
 * A SimpleFirmFactory produces an unlimited numer of identical firms which
 * all produce the same good using the same production function.
 * 
 * @author kkoning
 *
 */
public class SimpleFirmFactory implements FirmFactory {
	private Good good;
	private ProductionFunction pf;
	
	public SimpleFirmFactory(Good good, ProductionFunction pf) {
		this.good = good;
		this.pf = pf;
	}
	
	@Override
	public Firm getFirm() {
		
		
		return null;
	}
	
	
	
	
}
