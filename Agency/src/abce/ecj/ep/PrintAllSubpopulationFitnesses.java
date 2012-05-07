package abce.ecj.ep;


import ec.*;
import ec.util.Parameter;
import evoict.*;
import evoict.ep.*;
import evoict.io.*;



public class PrintAllSubpopulationFitnesses implements Procedure {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	boolean						untriggered			= true;
	DelimitedOutFile			out					= null;
	String						prefix;
	final String				format				= "Generation%d,Subpopulation%d,GridRows%d,GridColumns%d,Fitnesses%s";



	@Override
	public void setup(EventProcedureArgs args) throws BadConfiguration {
		prefix = (args.containsKey("prefix")) ? args.get("prefix") : "ecj";

	}



	@Override
	public void process(Object... context) throws Exception {
		EvolutionState state = (EvolutionState) context[0];
		if (untriggered) {
			untriggered = false;
			String dir = state.parameters.getString(new Parameter("output_dir"), null);
			if (!evoict.io.PathUtil.makeDirectory(dir)) {
				throw new RuntimeException("Unable to create output dirctory");
			}
			String path = dir + "/" + prefix + "-subpop_fitness.csv.gz";
			out = new DelimitedOutFile(path, format);
		}

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
		System.err.println("Flushing and closing.");
		if (out != null) {
			out.getWriter().flush();
			out.close();
		}
	}

}
