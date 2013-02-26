package ec.agency.stats;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

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
		
		String fileName = outFile + ".job" + evoState.job[0];
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
		Object[] o = new Object[size];
		o[0] = state.job[0];
		o[1] = state.generation;
		
		for (int i = 0; i < numSubpops; i++) {
			o[i+2] = state.population.subpops[i].individuals.length;
		}
		out.writeTuple(o);
		out.flush();
	}
	
}
