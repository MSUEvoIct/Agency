package abce.agency.data;


import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import sim.engine.SimState;
import sim.engine.Steppable;
import abce.agency.MarketSimulation;



/**
 * This is the new Reporter system for Simternet.
 * 
 * Each reporter may have zero or more "ReporterComponents", which are basically
 * prefixes to CSV files. This lets us have different levels of data reporting
 * (per-step, per-simulation, inside an evolutionary context, and outside)
 * without having to implement (and deal with elsewhere) a different reporter
 * class for each type.
 * 
 * Each ReporterComponent must return a String[] for getHeaders() and
 * getValues(). For each one of these components, the reporter object will query
 * for the list of headers or values, and prepend them (comma separated) to
 * whatever substantive data is logged by the specific Reporter.
 * 
 * Once a line of data is built, the Reporter writes it directly to a
 * BufferedCSVFile, rather than a log4j logger. This should allow the deployment
 * of more reporters without as much coordination/trouble because there will be
 * no need to separately configure each stream in log4j.properties. Each
 * BufferedCSVFile should only be written to by one <i>type</i> of reporter, but
 * an arbitrary number of reporters can write simultaneously to a single
 * BufferedCSVFile. This should allow efficient data reporting in a
 * multi-simternet evolutionary environment.
 * 
 * TODO: Rewrite comment for Agency
 * 
 * @author kkoning
 * 
 */
public abstract class CSVReporter implements Serializable, Steppable {

	private static final long		serialVersionUID	= 1L;

	protected transient PrintWriter	output;
	protected MarketSimulation		sim;

	public int						stepModulo;

	/**
	 * TODO: Currently has no effect
	 */
	public final boolean			printColumnHeaders;
	public final boolean			prependGeneration;



	public CSVReporter(PrintWriter outputTo, boolean printColumnHeaders,
			int stepModulo, MarketSimulation sim) {

		this.printColumnHeaders = printColumnHeaders;
		this.stepModulo = stepModulo;
		this.output = outputTo;
		this.sim = sim;
		if (sim != null) {
			prependGeneration = (sim.generation != null);

		} else {
			prependGeneration = false;
		}

	}



	protected final String buildCSVLine(Iterable<Object> elements) {
		StringBuffer sb = new StringBuffer();
		if (prependGeneration)
			sb.append(sim.generation + ",");

		sb.append(sim.simulationID + ",");
		sb.append(sim.getSteps() + ",");
		
		Iterator<Object> oi = elements.iterator();
		while (oi.hasNext()) {
			Object thing = oi.next();
			sb.append(thing);
			if (oi.hasNext()) {
				sb.append(",");
			}
		}
		return sb.toString();
	}



	protected final void outputCSVLine(Iterable<Object> elements) {
		String csvString = buildCSVLine(elements);
		output.println(csvString);
	}



	protected abstract List<Object> getHeaders();



	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub

	}



	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

}
