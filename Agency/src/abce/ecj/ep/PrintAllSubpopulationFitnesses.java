package abce.ecj.ep;


import abce.util.io.DelimitedOutFile;
import abce.util.events.EventProcedureArgs;
import abce.util.events.Procedure;
import abce.util.BadConfiguration;
import ec.Subpopulation;
import ec.util.Parameter;



public class PrintAllSubpopulationFitnesses implements Procedure {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	String						name;
	final String				format				= "Generation%d,Subpopulation%d,GridRows%d,GridColumns%d,Fitnesses%s";



	@Override
	public void setup(EventProcedureArgs args) throws BadConfiguration {
		name = (args.containsKey("name")) ? args.get("name") : "ecj";

	}



	@Override
	public void process(Object... context) throws Exception {
		EPSimpleEvolutionState state = (EPSimpleEvolutionState) context[0];
		String path = name + ".csv.gz";
		DelimitedOutFile out = state.file_manager.getDelimitedOutFile(path, format);

		Subpopulation[] subpops = state.population.subpops;
		for (int sp = 0; sp < subpops.length; sp++) {
			Parameter base = new Parameter("pop.subpop");
			Parameter subpop_param = base.push(String.valueOf(sp));
			int grid_x = (state.parameters.exists(subpop_param.push("grid_x"))) ? state.parameters.getInt(
					subpop_param.push("grid_x"), null) : -1;
			int grid_y = (state.parameters.exists(subpop_param.push("grid_y"))) ? state.parameters.getInt(
					subpop_param.push("grid_y"), null) : -1;

			StringBuffer buf = new StringBuffer();
			buf.append("[");
			int num_ind = subpops[sp].individuals.length;
			for (int ind = 0; ind < num_ind; ind++) {
				buf.append(subpops[sp].individuals[ind].fitness.fitness());
				if (ind < num_ind - 1) {
					buf.append(";");
				}
			}
			buf.append("]");
			out.write(state.generation, sp, grid_x, grid_y, buf.toString());
		}

	}



	@Override
	public void finish() {
	}

}
