package abce.agency.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import sim.engine.SimState;
import abce.agency.consumer.Consumer;
import abce.agency.engine.MarketSimulation;
import abce.agency.goods.Good;

public class SalesReporter extends CSVReporter {
	private static final long serialVersionUID = 1L;

	/*
	 * TODO-Matt: Extract me to a configuration file
	 */
	public static PrintWriter staticOut = null;
	static {
		File outFile = new File("SalesReporter.csv");
		try {
			staticOut = new PrintWriter(outFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public SalesReporter(PrintWriter outputTo, boolean printColumnHeaders,
			int stepModulo, MarketSimulation sim) {
		super(outputTo, printColumnHeaders, stepModulo, sim);
		this.output = staticOut;
	}

	@Override
	public void step(SimState state) {
		for (Consumer c : sim.getConsumers()) {
			for (Good g : c.allDesiredGoods()) {
				List<Object> data = new ArrayList<Object>();
				data.add(c.agentID);
				data.add(g.id);
				data.add(c.getPastQty(g, 0));
				data.add(c.getPastPaid(g, 0));
				this.outputCSVLine(data);
			}
		}
		output.flush();
	}

	@Override
	protected List<Object> getHeaders() {
		List<Object> headers = new ArrayList<Object>();
		headers.add("ConsumerID");
		headers.add("GoodID");
		headers.add("QuantityPurchased");
		headers.add("TotalPaid");

		return headers;
	}

}
