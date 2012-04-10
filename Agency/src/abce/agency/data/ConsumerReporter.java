package abce.agency.data;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import sim.engine.SimState;
import abce.agency.consumer.Consumer;
import abce.agency.engine.MarketSimulation;

public class ConsumerReporter extends CSVReporter {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param outputTo  Output CSV lines to this PrintWriter
	 * @param printColumnHeaders
	 * @param stepModulo  Output data every X steps
	 * @param sim  Reference to the MarketSimulation on which we are reporting.
	 */
	public ConsumerReporter(PrintWriter outputTo,
			boolean printColumnHeaders, int stepModulo, MarketSimulation sim) {
		super(outputTo, printColumnHeaders, stepModulo, sim);
	}

	@Override
	public final void step(SimState state) {
		for (Consumer c : sim.getConsumers()) {
			Iterable<Object> data = getData(c);
			outputCSVLine(data);
		}
		this.output.flush();
	}

	@Override
	protected List<Object> getHeaders() {
		ArrayList<Object> headers = new ArrayList<Object>();
		headers.add("ConsumerID");
		headers.add("Population");
		return headers;
	}
	
	protected List<Object> getData(Consumer c) {
		// Ugh... seems wasteful.  :/
		ArrayList<Object> toPrepend = new ArrayList<Object>();
		toPrepend.add(c.agentID);
		toPrepend.add(c.getPopulation());
		return toPrepend;
	}

}
