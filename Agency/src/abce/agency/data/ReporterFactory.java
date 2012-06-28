package abce.agency.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import sim.engine.Steppable;
import abce.agency.MarketSimulation;

public abstract class ReporterFactory {

	public static final int defaultBufferSize = 32768; // 32KiB buffer by default
	
	public ReporterFactory() {
		
	}
	
	public abstract List<Steppable> createReporters(MarketSimulation sim);
	
	protected PrintWriter createPrintWriter(File outputFile) {
		PrintWriter toReturn = null;
		
		try {
			// Create the parent directories, if necessary
			File outputDir = new File(outputFile.getCanonicalPath());
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			FileWriter fw = new FileWriter(outputFile);
			BufferedWriter bw = new BufferedWriter(fw, defaultBufferSize);
			toReturn = new PrintWriter(bw);
		} catch (IOException e) {
			System.err.println(e);
			System.exit(0);
		}

		if (toReturn == null) {
			throw new RuntimeException("Reporter output file should be open but is null.  Why?");
		}
		
		return toReturn;
	}
	
	
}
