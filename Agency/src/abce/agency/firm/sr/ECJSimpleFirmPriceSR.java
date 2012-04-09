package abce.agency.firm.sr;


import abce.agency.*;
import abce.agency.consumer.*;
import abce.agency.ec.*;
import abce.agency.finance.*;
import abce.agency.firm.*;
import abce.agency.goods.*;
import evoict.reflection.*;



public class ECJSimpleFirmPriceSR implements StimulusResponse {

	static final Class<?>[]					allowed_classes	= { Integer.class, int.class, Double.class, double.class };
	static final RestrictedMethodDictionary	static_dict		=
																	new RestrictedMethodDictionary(
																			ECJSimpleFirmPriceSR.class, 3,
																			allowed_classes);

	@Stimulus(name = "Firm")
	public ECJSimpleFirm					_firm;

	@Stimulus(name = "Account")
	public Accounts							_account;

	@Stimulus(name = "Good")
	public Good								_good;

	@Stimulus(name = "Market")
	public Market							_market;



	public ECJSimpleFirmPriceSR(ECJSimpleFirm firm, Market m, Good g) {
		_firm = firm;
		_account = firm.getAccounts();
		_good = g;
		_market = m;
	}



	@Response
	public void adjustPrice(double price_perc) {
		_firm.adjustPrice(price_perc);
	}



	@Stimulus(name = "Inventory")
	public double inventory() {
		return _firm.getInventory(_good);
	}



	@Stimulus(name = "AvgMarketPrice")
	public double avgMarketPrice() {
		double total_price = 0.0;
		int num_firms = 0;
		try {
			for (Consumer c : _market.getConsumers()) {
				for (Firm f : _market.getFirms()) {
					Offer offer = f.getOffer(_good, c);
					if (offer != null) {
						total_price += f.getOffer(_good, c).price;
						num_firms++;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		double retval = (num_firms != 0) ? total_price / num_firms : 0.0;
		// System.err.println(retval);

		return retval;
	}



	@Override
	public MethodDictionary dictionary() {
		return static_dict;
	}
}