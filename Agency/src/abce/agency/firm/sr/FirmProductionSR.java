package abce.agency.firm.sr;


import abce.agency.*;
import abce.agency.ec.*;
import abce.agency.firm.*;
import abce.agency.goods.*;



public interface FirmProductionSR extends MarketSimulationSR {

	public void setup(ECFirm f, Market m, Good g);
}
