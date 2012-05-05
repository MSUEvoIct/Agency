package abce.agency.consumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sim.engine.SimState;
import abce.agency.Offer;
import abce.agency.actions.SaleOfGoods;
import abce.agency.firm.Firm;
import abce.agency.goods.Good;

public class ReluctantSwitcher extends Consumer {
	private static final long serialVersionUID = 1L;

	protected Map<Good, Offer> lastOfferAccepted = new HashMap<Good, Offer>();

	/**
	 * If a specific per-good amount is not specified, this amount is used.
	 */
	protected double defaultAmount = 0.0;
	/**
	 * If an amount is specified by good, it is stored in this map.
	 */
	protected Map<Good, Double> amountsByGood;

	/**
	 * If a specific per-good percentage is not specified, this percentage is
	 * used.
	 */
	protected double defaultPercentage = 0.0;
	/**
	 * If a percentage is specified by good, it is stored in this map.
	 */
	protected Map<Good, Double> percentagesByGood;

	/**
	 * Determines which price comparison mode will be used. See the variable
	 * JavaDoc for a description of the different modes.
	 */
	protected Mode mode = Mode.PERCENTAGE;

	public enum Mode {
		/**
		 * This consumer will purchase goods from the same producer as in the
		 * last period unless the newly offered price is at least the specified
		 * percentage lower than the price offered by its previous supplier in
		 * the current time step.
		 */
		PERCENTAGE,

		/**
		 * This consumer will purchase goods from the same producer as in the
		 * last period unless the newly offered price is at least the specified
		 * amount lower than the price offered by its previous supplier in the
		 * current time step.
		 */
		AMOUNT,
	}

	public ReluctantSwitcher(double population) {
		super(population);
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	@Override
	public void step(SimState state) {
		super.step(state);
		
		List<Offer> orderedOffers = null;

		// For all goods we want to consume...
		for (Good g : allDesiredGoods()) {
			// Find the offer from the past supplier, if any.
			Offer oldOffer = lastOfferAccepted.get(g);
			if (oldOffer == null) {
				// Just make the ideal purchase for this good
				findAndConsumeIdeal(g);

			} else {
				// There is an existing firm.
				// Determine their offier
				Offer newOffer = oldOffer.firm.getOffer(oldOffer.market, this);

				// Get the list of all offers
				List<Offer> bestOffers = this.getSortedOffers(g);
				Offer bestOffer = bestOffers.get(0);
				
				boolean goWithNewFirm = doSupplierSwitch(bestOffer,newOffer);
				if (goWithNewFirm) {
					orderedOffers = bestOffers;
				} else {
					bestOffers.remove(newOffer); // make sure the old offer isn't included twice.
					// can't do this by object; they are two different offer objects.  once from the initial probe
					// and once again when looking at all offers.
					orderedOffers = new ArrayList<Offer>();
					orderedOffers.add(newOffer);
					orderedOffers.addAll(bestOffers);
				}
				consumeOrderedOffers(orderedOffers,this.getPopulation());
			}
		}
	}

	protected boolean doSupplierSwitch(Offer fromNewFirm, Offer fromOldFirm) {
		Good g = fromOldFirm.market.good;
		
		if (this.mode.equals(Mode.AMOUNT)) {
			double amount = defaultAmount;
			if (amountsByGood != null) {
				Double specificAmount = amountsByGood.get(g);
				if (specificAmount != null)
					amount = specificAmount;
			}
			// how much less is the cheapest offer?
			double priceDifference = fromOldFirm.price - fromNewFirm.price;
			if (priceDifference > amount)
				return true;
			else
				return false;
		} else if (this.mode.equals(Mode.PERCENTAGE)) {
			double percentage = defaultPercentage;
			if (percentagesByGood != null) {
				Double specificPercentage = percentagesByGood.get(g);
				if (specificPercentage != null)
					percentage = specificPercentage;
			}
			// how much less is the cheapest offer?
			double priceDifference = fromOldFirm.price - fromNewFirm.price;
			double priceDifRatio = priceDifference / fromOldFirm.price;
			if (priceDifRatio > (percentage / 100))
				return true;
			else
				return false;
		} else { // Should never happen, it it does, throw error and debug to
					// figure out why
			throw new RuntimeException();
		}
	}

	/*
	 * In addition to the normal processing, keep track of which suppliers we
	 * used. (non-Javadoc)
	 * 
	 * @see
	 * abce.agency.consumer.Consumer#execute(abce.agency.actions.SaleOfGoods)
	 */
	@Override
	public void actualize(SaleOfGoods saleOfGoods) {
		super.actualize(saleOfGoods);
		lastOfferAccepted.put(saleOfGoods.offer.market.good, saleOfGoods.offer);
	}

	public void setDefaultPercentage(double percentage) {
		this.defaultPercentage = percentage;
	}

	public void setDefaultAmount(double amount) {
		this.defaultAmount = amount;
	}

	public void setAmount(Good good, double amount) {
		if (amountsByGood == null)
			amountsByGood = new HashMap<Good, Double>();
		amountsByGood.put(good, amount);

	}

	public void setPercentage(Good good, double amount) {
		if (percentagesByGood == null)
			percentagesByGood = new HashMap<Good, Double>();
		percentagesByGood.put(good, amount);
	}

}
