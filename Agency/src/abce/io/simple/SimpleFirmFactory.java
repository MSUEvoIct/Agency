package abce.io.simple;

import abce.agency.firm.Firm;
import abce.agency.firm.FirmFactory;
import abce.agency.goods.Good;
import abce.agency.production.ProductionFunction;

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
