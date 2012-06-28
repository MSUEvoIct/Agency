package abce.ecj.ep;


import java.io.IOException;

import abce.agency.events.EventProcedureArgs;
import abce.agency.events.Procedure;
import abce.agency.util.io.GZOutFile;
import ec.EvolutionState;
import ec.util.Parameter;



public class PrintPopulation implements Procedure {

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;
	String							prefix				= null;
	protected GZOutFile				fot					= null;
	protected EventProcedureArgs	args				= null;
	protected boolean				untriggered			= true;



	@Override
	public void process(Object... context) {
		EvolutionState state = (EvolutionState) context[0];
		String dir = state.parameters.getString(new Parameter("outdir"), null) + "/";
		String filename = prefix + "-" + String.valueOf(state.generation) + ".pop.gz";
		try {
			fot = new GZOutFile(dir + filename);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		state.population.printPopulation(state, fot.getWriter());
		fot.close();
	}



	@Override
	public void finish() {
		if (fot != null) {
			fot.close();
		}
	}



	@Override
	public void setup(EventProcedureArgs args) {
		prefix = args.containsKey("path") ? args.get("prefix") : "population-";
	}
}
