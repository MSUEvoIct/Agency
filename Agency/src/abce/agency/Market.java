package abce.agency;


import java.io.*;
import java.util.*;

import abce.agency.actions.*;
import abce.agency.consumer.*;
import abce.agency.firm.*;
import abce.agency.goods.*;
import evoict.reflection.*;



public class Market implements Serializable {
	private static final long	serialVersionUID	= 1L;

	private static int idSequence = 0;
	public final int id;
	
	// Markets are for exactly one good.
	public final Good			good;

	private final Set<Firm>		firms				= new LinkedHashSet<Firm>();
	private final Set<Consumer>	consumers			= new LinkedHashSet<Consumer>();



	public Market(Good good) {
		id = idSequence++;
		
		if (good == null)
			throw new RuntimeException("Cannot have a market for a null good");
		this.good = good;
	}



	public Firm[] getFirms() {
		return firms.toArray(new Firm[firms.size()]);
	}



	public Consumer[] getConsumers() {
		return consumers.toArray(new Consumer[consumers.size()]);
	}



	public List<Offer> getOffers(Consumer c, Good g) {
		List<Offer> offers = new ArrayList<Offer>();
		for (Firm f : firms) {
			Offer offer = f.getOffer(g, c);
			if (offer != null)
				offers.add(offer);
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



	@Stimulus(name = "NumberOfFirms")
	public int getNumberOfFirms() {
		return firms.size();
	}



	@Stimulus(name = "NumberOfAgents")
	public int getNumberOfConsumerAgents() {
		return consumers.size();
	}



	@Stimulus(name = "NumberOfPeople")
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
