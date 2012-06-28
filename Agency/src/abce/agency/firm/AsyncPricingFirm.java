package abce.agency.firm;


import java.util.LinkedHashMap;
import java.util.Map;

import abce.agency.async.AsyncUpdate;
import abce.agency.goods.Good;



public abstract class AsyncPricingFirm extends Firm implements AsyncUpdate {

	private static final long	serialVersionUID	= 1L;

	protected Map<Good, Double>	futurePrices		= new LinkedHashMap<Good, Double>();



	@Override
	public void update() {
		// Update all the current prices with the (possibly) changed versions
		for (Good good : futurePrices.keySet()) {
			prices.put(good, futurePrices.get(good));
		}
	}

}
