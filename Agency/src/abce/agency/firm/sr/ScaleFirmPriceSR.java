package abce.agency.firm.sr;


import abce.agency.Market;
import abce.agency.Offer;
import abce.agency.consumer.Consumer;
import abce.agency.finance.Accounts;
import abce.agency.firm.ECProdPriceFirm;
import abce.agency.firm.Firm;
import abce.agency.goods.Good;
import abce.util.reflection.MethodDictionary;
import abce.util.reflection.Response;
import abce.util.reflection.RestrictedMethodDictionary;
import abce.util.reflection.Stimulus;



public class ScaleFirmPriceSR implements FirmPricingSR {

	static final Class<?>[]					allowed_classes	= { Integer.class, int.class, Double.class, double.class };
	static final RestrictedMethodDictionary	static_dict		=
																	new RestrictedMethodDictionary(
																			ScaleFirmPriceSR.class, 3,
																			allowed_classes);

	@Stimulus(name = "Firm")
	public ECProdPriceFirm					_firm;

	@Stimulus(name = "Account")
	public Accounts							_account;

	@Stimulus(name = "Good")
	public Good								_good;

	@Stimulus(name = "Market")
	public Market							_market;


	public ScaleFirmPriceSR() {
	}



	@Override
	public void setup(ECProdPriceFirm f, Market m, Good g) {
		_firm = f;
		_market = m;
		_good = g;
		_account = f.getAccounts();
	}



	@Response
	public void scalePrice(double price_perc) {
		_firm.scalePriceCarrying(_market, _good, price_perc);
	}


	@Stimulus(name = "LastProduction")
	public double productionLastPeriod() {
		return _firm.getProduction(_good, 1);
	}



	@Stimulus(name = "TotalMarketInventory")
	public double totalInventory() {
		double total = 0.0;
		for (Firm f : _market.getFirms()) {
			if (f.hasProduced(_good)) {
				total += f.getInventory(_good,1);
			}
		}
		return total;
	}



	@Stimulus(name = "Inventory")
	public double inventory() {
		return _firm.getInventory(_good,1);
	}



	@Stimulus(name = "LastPrice")
	public double getPrice() {
		return _firm.getPrice(_good, null, 1);
	}



	@Stimulus(name = "AvgMarketPrice")
	public double avgMarketPrice() {
		double total_price = 0.0;
		int num_firms = 0;
		try {
			for (Consumer c : _market.getConsumers()) {
				for (Firm f : _market.getFirms()) {
					Offer offer = f.getOffer(_market, c);
					if (offer != null) {
						total_price += offer.price;
						num_firms++;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		double retval = (num_firms != 0) ? total_price / num_firms : 0.0;
		return retval;
	}



	@Override
	public MethodDictionary dictionary() {
		return static_dict;
	}

}