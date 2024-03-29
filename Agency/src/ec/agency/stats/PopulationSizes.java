package ec.agency.stats;

import ec.EvolutionState;
import ec.Statistics;
import ec.agency.io.DataOutputFile;
import ec.util.Parameter;

public class PopulationSizes extends Statistics {
	private static final long serialVersionUID = 1L;

	static final String outFile = "popSizes";
	String filename;

	boolean fileOpened = false;
	boolean headerOutput = false;

	DataOutputFile out;

	@Override
	public void setup(EvolutionState evoState, Parameter base) {
		super.setup(evoState, base);

		String fileName;
		if (evoState.job != null)
			fileName = outFile + ".job" + evoState.job[0];
		else
			fileName = outFile;

		out = new DataOutputFile(fileName);

	}

	public void finalStatistics(EvolutionState state, int result) {
		super.finalStatistics(state, result);

		// Just need to clean up out output files here
		out.flush();
		out.close();
	}

	@Override
	public void postEvaluationStatistics(EvolutionState state) {
		super.postBreedingStatistics(state);

		int numSubpops = state.population.subpops.length;
		int size = numSubpops + 2;
		if (!headerOutput) {
			String[] headers = new String[size];
			headers[0] = "Job";
			headers[1] = "Generation";
			for (int i = 0; i < numSubpops; i++) {
				String indName = state.population.subpops[i].species.i_prototype
						.getClass().getSimpleName();
				headers[i + 2] = "SubPop" + i + "_" + indName;

			}
			out.writeTuple(headers);
			headerOutput = true;
		}

		Object[] o = new Object[size];
		if (state.job != null)
			o[0] = state.job[0];
		else
			o[0] = null;
		o[1] = state.generation;

		for (int i = 0; i < numSubpops; i++) {
			o[i + 2] = state.population.subpops[i].individuals.length;
		}
		out.writeTuple(o);
		out.flush();
	}

}
