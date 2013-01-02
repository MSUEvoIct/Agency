package abce.agency.events;


import abce.util.io.DelimitedOutFile;
import abce.util.events.EventProcedureArgs;
import abce.util.events.Procedure;
import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;



public class PrintIndividualFitnesses implements Procedure {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	protected String			path;
	protected final String		format				= "Generation%d,Subpopulation%d,Individual%d,Fitness%0.2f";
	protected boolean			untriggered			= true;
	protected DelimitedOutFile	out;



	@Override
	public void setup(EventProcedureArgs args) {
		path = args.containsKey("path") ? args.get("path") : "fitness.csv.gz";
	}



	@Override
	public void process(Object... context) throws Exception {
		EvolutionState state = (EvolutionState) context[0];
		if (untriggered) {
			out = new DelimitedOutFile(path, format);
		}
		for (int sp = 0; sp < state.population.subpops.length; sp++) {
			Subpopulation subpop = state.population.subpops[sp];
			for (int ind = 0; ind < subpop.individuals.length; ind++) {
				Individual individual = subpop.individuals[ind];
				out.write(state.generation, sp, ind, individual.fitness.fitness());
			}

		}

	}



	@Override
	public void finish() {
		if (out != null) {
			out.close();
		}
	}

}
