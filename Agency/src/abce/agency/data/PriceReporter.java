package abce.agency.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import sim.engine.SimState;
import abce.agency.Market;
import abce.agency.Offer;
import abce.agency.engine.MarketSimulation;
import abce.agency.firm.Firm;

public class PriceReporter extends CSVReporter {
	private static final long serialVersionUID = 1L;

	public static PrintWriter staticOut = null;
	
	/*
	 * TODO-Matt: Extract me to a configuration file
	 */
	static {
		File outFile = new File("PriceReporter.csv");
		try {
			staticOut = new PrintWriter(outFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param outputTo  Output CSV lines to this PrintWriter
	 * @param printColumnHeaders
	 * @param stepModulo  Output data every X steps
	 * @param sim  Reference to the MarketSimulation on which we are reporting.
	 */
	public PriceReporter(PrintWriter outputTo, boolean printColumnHeaders,
			int stepModulo, MarketSimulation sim) {
		super(outputTo, printColumnHeaders, stepModulo, sim);
		
		this.output = staticOut;
	}

	@Override
	public void step(SimState state) {
		for (Market m : sim.getMarkets()) {
			for (Firm f : m.getFirms()) {
				List<Object> data = new ArrayList<Object>();
				data.add(m.good.id);
				data.add(m.id);
				data.add(f.agentID);
				
				// Price
				Double price = null;
				Offer o = f.getOffer(m.good, null);
				if (o != null) 
					price = o.price;
				if (price > 200) 
					price = 200.0;
				data.add(price);
				
				outputCSVLine(data);
			}
			this.output.flush();
		}
		
		
		// TODO Auto-generated method stub

	}

	@Override
	protected List<Object> getHeaders() {
		ArrayList<Object> headers = new ArrayList<Object>();
		headers.add("goodID");
		headers.add("marketID");
		headers.add("firmID");
		headers.add("price");
		return null;
	}

}
