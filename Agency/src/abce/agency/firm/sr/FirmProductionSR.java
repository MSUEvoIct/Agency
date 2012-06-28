package abce.agency.firm.sr;


import abce.agency.Market;
import abce.agency.ec.MarketSimulationSR;
import abce.agency.firm.ECProdPriceFirm;
import abce.agency.goods.Good;



public interface FirmProductionSR extends MarketSimulationSR {

	public void setup(ECProdPriceFirm f, Market m, Good g);
}
