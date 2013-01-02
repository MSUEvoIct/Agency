package ec.agency.events;


import java.io.IOException;

import ec.EvolutionState;
import ec.agency.io.GZOutFile;
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
		EPSimpleEvolutionState state = (EPSimpleEvolutionState) context[0];
		String filename =  state.file_manager.makePath("Generation-" + String.valueOf(state.generation) + ".pop.gz");
		try {
			fot = new GZOutFile(filename);
			state.population.printPopulation(state, fot.getWriter());
			fot.close();
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
