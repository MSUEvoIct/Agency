package ec.agency.stats;

import java.util.ArrayList;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.Statistics;
import ec.Subpopulation;
import ec.agency.io.DataOutputFile;
import ec.util.Parameter;
import ec.vector.BitVectorIndividual;
import ec.vector.FloatVectorIndividual;
import ec.vector.VectorIndividual;

public class GenomeDump extends Statistics {
	private static final long serialVersionUID = 1L;
	
	static final String jobHeaderPrefix = "Job";
	static final String genHeaderPrefix = "Generation";
	static final String indHeaderPrefix = "Individual";
	static final String indFitnessPrefix = "Fitness";
	static final String lociHeaderPrefix = "Loci";

	boolean filesOpened = false;
	DataOutputFile[] out;
	boolean[] headersOutput;

	@Override
	public void setup(EvolutionState evoState, Parameter base) {
		super.setup(evoState, base);
		/* 
		 * Nothing to set up; operation inferred from the evoState,
		 * file names hard coded
		 */

	}
	
	/**
	 * Open one file per subpopulation
	 * 
	 * @param evoState
	 */
	private void openFiles(EvolutionState evoState) {
		int numSubpops = evoState.population.subpops.length;

		out = new DataOutputFile[numSubpops];
		headersOutput = new boolean[numSubpops];
		
		for (int i = 0; i < numSubpops; i++) {
			headersOutput[i] = false;
			
			// Get a test individual, see if we support it
			Subpopulation spop = evoState.population.subpops[i];
			Individual ind = spop.individuals[0];
			boolean supported = individualSupported(ind);
			if (supported) {
				// then open a file for this subpop

				String fileName = null;
				
				if (evoState.job != null) {
					fileName = "genome.job" + evoState.job[0] + ".spop" + i + ".tsv";
				} else {
					fileName = "genome.spop" + i + ".tsv";
				}
				
				out[i] = new DataOutputFile(fileName);
			}
		}
	}

	private boolean individualSupported(Individual o) {
		if (o instanceof FloatVectorIndividual)
			return true;
		
		if (o instanceof BitVectorIndividual)
			return true;
		
		return false;
	}
	
	private int getNumLoci(Individual ind) {
		int numLoci = -1; // i.e., impossible value; not supported
		
		if (ind instanceof VectorIndividual) {
			VectorIndividual vi = (VectorIndividual) ind;
			numLoci = vi.genomeLength();
		}
		
		return numLoci;
	}
	
	@Override
	public void finalStatistics(EvolutionState state, int result) {
		super.finalStatistics(state, result);
		// Just need to clean up out output files here
		for (DataOutputFile dof : out) {
			if (dof != null) {
				dof.flush();
				dof.close();
			}
		}
	}

	@Override
	public void postEvaluationStatistics(EvolutionState state) {
		super.postEvaluationStatistics(state);
	
		if (!filesOpened) {
			openFiles(state);
			filesOpened = true;
		}
		
		// Iterate through the subpopulations, doing separate stats for each
		int numSubPops = state.population.subpops.length;
		for (int i = 0; i < numSubPops; i++) {
			perSubPopulationDump(state, i);
		}
		
	}

	private void perSubPopulationDump(EvolutionState state, int subPopIdx) {
		Subpopulation spop = state.population.subpops[subPopIdx];
		int numInds = spop.individuals.length;
		int numLoci = getNumLoci(state.population.subpops[subPopIdx].individuals[0]);
		
		// print header first
		if (!headersOutput[subPopIdx]) {
			Object[] headers = getHeaders(numLoci);
			out[subPopIdx].writeTuple(headers);
			headersOutput[subPopIdx] = true;
		}
		
		
		Object job = null;
		if (state.job != null)
			job = state.job[0];
		int gen = state.generation; 
		
		for (int i = 0; i < numInds; i++) {
			ArrayList<Object> toOutput = new ArrayList<Object>();

			toOutput.add(job);
			toOutput.add(gen);
			toOutput.add(i);
			// TODO Deal with Fitnesses better?
			Fitness fit = spop.individuals[i].fitness;
			toOutput.add(fit.fitness());
			
			// TODO Fix for things other than FloatVectorIndividuals
			if (spop.individuals[i] instanceof FloatVectorIndividual) {
				FloatVectorIndividual fvi = (FloatVectorIndividual) spop.individuals[i];
				for (int j = 0; j < numLoci; j++) {
					toOutput.add(fvi.genome[j]);
				}
			} else if (spop.individuals[i] instanceof BitVectorIndividual) {
				BitVectorIndividual bvi = (BitVectorIndividual) spop.individuals[i];
				for (int j = 0; j < numLoci; j++) {
					if (bvi.genome[j])
						toOutput.add("1");
					else
						toOutput.add("0");
				}
			}
			
			out[subPopIdx].writeTuple(toOutput.toArray());
		}
		
		
	}
	
	private Object[] getHeaders(int numLoci) {
		int size = 4 + numLoci;
		Object[] headers = new Object[size];
		headers[0] = jobHeaderPrefix;
		headers[1] = genHeaderPrefix;
		headers[2] = indHeaderPrefix;
		headers[3] = indFitnessPrefix;
		for (int i = 4; i < size; i++) {
			headers[i] = lociHeaderPrefix + (i - 4);
		}
		return headers;
	}
	

}
