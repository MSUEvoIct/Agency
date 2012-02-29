package abce.agency;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import abce.agency.actions.MarketEntry;
import abce.agency.consumer.Consumer;
import abce.agency.firm.Firm;
import abce.agency.goods.Good;

public class Market implements Serializable {
	private static final long serialVersionUID = 1L;

	// Markets are for exactly one good.
	public final Good good;

	private Set<Firm> firms = new HashSet<Firm>();
	private Set<Consumer> consumers = new HashSet<Consumer>();

	public Market(Good good) {
		if (good == null)
			throw new RuntimeException("Cannot have a market for a null good");
		this.good = good;
	}

	public List<Offer> getOffers(Consumer c, Good g) {
		List<Offer> offers = new ArrayList<Offer>();
		for (Firm f : firms) {
			if (f.produces(g)) {
				double availQty = f.getInventory(g); 
				if (availQty > 0.0) {
					Offer offer = new Offer(f,g,f.getPrice(g, c),availQty);
					offers.add(offer);
				}
			}
		}
		return offers;
	}
	
	/**
	 * The specified consumer is indicating that it will no longer participate
	 * in this market; when market information is gathered, this consumer will
	 * not be included.
	 * 
	 * @param consumer
	 */
	public void exit(Consumer consumer) {
		consumers.remove(consumer);
	}
	
	public int getNumberOfFirms() {
		return firms.size();
	}
	
	public int getNumberOfConsumerAgents() {
		return consumers.size();
	}

	public double getNumberOfPeople() {
		double people = 0.0;
		for (Consumer c : consumers) {
			people += c.getPopulation();
		}
		return people;
	}

	public void execute(MarketEntry marketEntry) {
		if (marketEntry.firm != null)
			firms.add(marketEntry.firm);
		if (marketEntry.consumer != null)
			consumers.add(marketEntry.consumer);
	}

	
	
}
