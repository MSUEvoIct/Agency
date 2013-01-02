package abce.agency.firm.sr;


import ec.agency.Market;
import ec.agency.MarketSimulationSR;
import ec.agency.goods.Good;
import abce.agency.firm.ECProdPriceFirm;



public interface FirmPricingSR extends MarketSimulationSR {

	public void setup(ECProdPriceFirm f, Market m, Good g);
}
