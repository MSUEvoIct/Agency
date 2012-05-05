package abce.agency.firm.sr;

import java.io.Serializable;

import evoict.reflection.MethodDictionary;
import evoict.reflection.Response;
import evoict.reflection.RestrictedMethodDictionary;
import evoict.reflection.Stimulus;
import abce.agency.Market;
import abce.agency.Offer;
import abce.agency.consumer.Consumer;
import abce.agency.ec.StimulusResponse;
import abce.agency.finance.Accounts;
import abce.agency.firm.ECJSimpleFirm;
import abce.agency.firm.Firm;
import abce.agency.goods.Good;

public class ECJSimpleFirmProductionSR implements StimulusResponse, Serializable {
	private static final long serialVersionUID = 1L;

	static final Class<?>[]					allowed_classes	= { Integer.class, int.class, Double.class, double.class };
	static final RestrictedMethodDictionary	static_dict		=
																	new RestrictedMethodDictionary(
																			ECJSimpleFirmProductionSR.class, 3,
																			allowed_classes);

	public ECJSimpleFirmProductionSR(ECJSimpleFirm f, Market m) {
		_firm = f;
		_account = f.getAccounts();
		_good = m.good;
		_market = m;
	}
	
	@Stimulus(name = "Firm")
	public ECJSimpleFirm					_firm;

	@Stimulus(name = "Account")
	public Accounts							_account;

	@Stimulus(name = "Good")
	public Good								_good;

	@Stimulus(name = "Market")
	public Market							_market;
	
	@Stimulus(name = "Inventory")
	public double inventory() {
		return _firm.getInventory(_market);
	}

	@Stimulus(name = "MyProduction(t-1)")
	public double productionLastPeriod() {
		return _firm.getPastQtyProduced(_good, 1);
	}
	
	@Response
	public void adjustProduction(double prod_perc) {
		_firm.adjustProduction(prod_perc);
	}
	
	
	@Override
	public MethodDictionary dictionary() {
		return static_dict;
	}
}
