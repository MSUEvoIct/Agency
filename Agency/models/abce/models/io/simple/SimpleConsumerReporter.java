package abce.models.io.simple;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ec.agency.MarketSimulation;
import ec.agency.consumer.Consumer;
import ec.agency.data.ConsumerReporter;
import ec.agency.goods.Good;




public class SimpleConsumerReporter extends ConsumerReporter {

	private static final long	serialVersionUID	= 1L;

	private final Good			good;



	public SimpleConsumerReporter(PrintWriter outputTo,
			boolean printColumnHeaders, int stepModulo, MarketSimulation sim) {
		super(outputTo, printColumnHeaders, stepModulo, sim);
		System.err.println("\tBegin constructing SimpleConsumerReporter.");

		good = ((SingleGoodSimulation) sim).good;
		System.err.println("\tFinsihed constructing SimpleConsumerReporter.");
	}



	@Override
	protected List<Object> getData(Consumer c) {
		ArrayList<Object> data = new ArrayList<Object>();
		data.addAll(super.getData(c));
		data.add(c.getWTP(good));
		data.add(c.getPastQty(good, 0));
		data.add(c.getPastPaid(good, 0));
		data.add(c.getPastSurplus(good, 0));

		return data;
	}



	@Override
	protected List<Object> getHeaders() {
		ArrayList<Object> headers = new ArrayList<Object>();
		headers.addAll(super.getHeaders());
		headers.add("WTP");
		headers.add("QtyPurchased");
		headers.add("PricePaid");
		headers.add("Surplus");
		return headers;
	}

}
