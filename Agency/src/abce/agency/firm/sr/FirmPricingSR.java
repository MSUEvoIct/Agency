package abce.agency.firm.sr;


import abce.agency.*;
import abce.agency.ec.*;
import abce.agency.firm.*;
import abce.agency.goods.*;



public interface FirmPricingSR extends MarketSimulationSR {

	public void setup(ECProdPriceFirm f, Market m, Good g);
}
