package ec.agency.stats;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import ec.EvolutionState;
import ec.Individual;
import ec.Statistics;
import ec.Subpopulation;
import ec.util.Parameter;
import ec.vector.FloatVectorIndividual;
import ec.vector.VectorIndividual;

public class GenomeDumpCSV extends Statistics {
	private static final long serialVersionUID = 1L;

	// name of parameter in ecj configuration file
	static final String pFilePrefix = "prefix";
	
	
	static final String separator = ",";
	static final String fileSuffix = ".csv";
	static final String subPopIndicator = ".subpop";
	static final String genHeaderPrefix = "Generation";
	static final String indHeaderPrefix = "Individual";
	static final String lociHeaderPrefix = "loci";

	
	boolean filesOpened = false;
	String filePrefix;
	File[] dumpFiles;
	PrintStream[] out;
	

	@Override
	public void setup(EvolutionState evoState, Parameter base) {
		super.setup(evoState, base);

		filePrefix = evoState.parameters
				.getString(base.push(pFilePrefix), null);

	}
	
	/**
	 * Open one file per subpopulation
	 * 
	 * @param evoState
	 */
	private void openFiles(EvolutionState evoState) {
		int numSubpops = evoState.population.subpops.length;
		dumpFiles = new File[numSubpops];
		out = new PrintStream[numSubpops];
		
		for (int i = 0; i < numSubpops; i++) {
			// Get a test individual, see if we support it
			Subpopulation spop = evoState.population.subpops[i];
			Individual ind = spop.individuals[0];
			boolean supported = individualSupported(ind);
			if (supported) {
				// then open a file for this subpop
				String fileName = filePrefix + subPopIndicator + i + fileSuffix;
				File toOpen = new File(fileName);
				dumpFiles[i] = toOpen;
				
				try {
					OutputStream os = new FileOutputStream(toOpen);
					BufferedOutputStream bos = new BufferedOutputStream(os);
					PrintStream ps = new PrintStream(bos);
					out[i] = ps;
					ps.println(getHeader(ind));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				
				
			}
			
			
			
		}
		
		
		
	}
	
	private String getHeader(Individual ind) {
		boolean supported = individualSupported(ind);
		if (!supported)
			throw new RuntimeException("Need a VectorIndividual for now probably");
		
		int numLoci = getNumLoci(ind);
		StringBuffer header = new StringBuffer();
		header.append(genHeaderPrefix);
		header.append(separator);
		header.append(indHeaderPrefix);
		header.append(separator);
		
		for (int i = 0; i < numLoci; i++) {
			header.append(lociHeaderPrefix + i);
			if (i + 1 < numLoci)
				header.append(separator);
			
		}
		
		return header.toString();
		
	}
	

	private boolean individualSupported(Individual o) {
		if (o instanceof FloatVectorIndividual)
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
		for (PrintStream ps : out) {
			if (ps != null) {
				ps.flush();
				ps.close();
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
		
		// Flush the buffers at least once per generation
		for (PrintStream ps : out) {
			if (ps != null) {
				ps.flush();
			}
		}
		
		
		
	}

	private void perSubPopulationDump(EvolutionState state, int subPopIdx) {
		Subpopulation spop = state.population.subpops[subPopIdx];
		int numInds = spop.individuals.length;
		int numLoci = getNumLoci(state.population.subpops[subPopIdx].individuals[0]);
		
		for (int i = 0; i < numInds; i++) {
			StringBuffer sb = new StringBuffer();
			sb.append(state.generation);
			sb.append(separator);
			sb.append(i);
			sb.append(separator);
			
			// TODO Fix for things other than FloatVectorIndividuals
			FloatVectorIndividual fvi = (FloatVectorIndividual) spop.individuals[i];
			
			for (int j = 0; j < numLoci; j++) {
				sb.append(fvi.genome[j]);
				if (j + 1 < numLoci)
					sb.append(separator);
				
			}
			
			out[subPopIdx].println(sb.toString());

		}
		
		
	}

}
