package ec.agency.stats;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import ec.EvolutionState;
import ec.Statistics;
import ec.util.Parameter;

public class PopulationSizes extends Statistics {
	private static final long serialVersionUID = 1L;

	static final String genHeader = "Generation";
	static final String popHeaderPrefix = "SPop";
	static final String separator = ",";

	static final String pFilename = "file";
	String filename;
	
	boolean fileOpened = false;
	boolean headerOutput = false;
	
	File outFile;
	PrintStream out;
	
	@Override
	public void setup(EvolutionState evoState, Parameter base) {
		super.setup(evoState, base);

		filename = evoState.parameters
				.getString(base.push(pFilename), null);
		
		OutputStream os;
		BufferedOutputStream bos;
		PrintStream ps;
				
		try {
			outFile = new File(filename);
			os = new FileOutputStream(outFile);
			bos = new BufferedOutputStream(os);
			out = new PrintStream(bos);
			fileOpened = true;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}

	}

	public void finalStatistics(EvolutionState state, int result) {
		super.finalStatistics(state, result);
		// Just need to clean up out output files here
		out.flush();
		out.close();
	}

	@Override
	public void postBreedingStatistics(EvolutionState state) {
		super.postBreedingStatistics(state);
		if (!headerOutput) {
			String header = fileHeader(state);
			out.println(header);
			headerOutput = true;
		}
		
		StringBuffer line = new StringBuffer();
		line.append(state.generation);
		line.append(separator);

		int numSubpops = state.population.subpops.length;
		
		for (int i = 0; i < numSubpops; i++) {
			line.append(state.population.subpops[i].individuals.length); // # individuals
			if (i < (numSubpops - 1))
				line.append(separator);
		}
		out.println(line);
		out.flush();
	}
	
	
	String fileHeader(EvolutionState state) {
		StringBuffer header = new StringBuffer();
		header.append(genHeader);
		header.append(separator);
		
		int numSubpops = state.population.subpops.length;
		
		for (int i = 0; i < numSubpops; i++) {
			header.append(popHeaderPrefix);
			header.append(i);
			if (i < (numSubpops - 1))
				header.append(separator);
		}
		
		return header.toString();
	}
	
	
	
	
}
