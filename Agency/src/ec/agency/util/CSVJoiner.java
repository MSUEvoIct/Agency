package ec.agency.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CSVJoiner implements Runnable {

	static final int bufferSize = 262144; // 256K buffers
	boolean firstFile = true;
	int stripFirst;
	int stripSubsequent;
	String inputListFile;
	String outputFile;
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CSVJoiner joiner = new CSVJoiner();
		Options options = initializeCLIOptions();
		
		CommandLineParser parser = new GnuParser();
		CommandLine cmdLine = null;
		
		try {
			cmdLine = parser.parse(options, args);
		} catch (ParseException e) {
			HelpFormatter help = new HelpFormatter();
			help.printHelp("test", options);
			System.exit(-1);
		}

		joiner.inputListFile = cmdLine.getOptionValue('i');
		joiner.outputFile = cmdLine.getOptionValue('o');
		joiner.stripFirst = Integer.parseInt(cmdLine.getOptionValue("stripFirst"));
		joiner.stripSubsequent = Integer.parseInt(cmdLine.getOptionValue("stripSubsequent"));

		joiner.run();
	}
	


	@Override
	public void run() {
		System.out.println("(in,out,strip) = (" + inputListFile + "," + outputFile + "," + stripFirst + "," + stripSubsequent + ")");
		
		// Open output file
		File outFile = null;
		OutputStream outStream = null;
		PrintStream out = null;

		File listFile = null;
		BufferedReader listReader = null;
		
		
		try {
			outFile = new File(outputFile);
			FileOutputStream fos = new FileOutputStream(outFile);
			outStream = new BufferedOutputStream(fos,bufferSize); 
			out = new PrintStream(outStream);

			listFile = new File(inputListFile);
			listReader = new BufferedReader(new FileReader(listFile),bufferSize);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		// Iterate through the different lines in the file
		String inputListLine = null;
		try {
			inputListLine = listReader.readLine();
		} catch (IOException e) {
			throw new RuntimeException("Error reading input file list");
		}

		while (inputListLine != null) {
			
			try {
				processInputFile(inputListLine,firstFile,out);
				inputListLine = listReader.readLine();
			} catch (IOException e) {
				throw new RuntimeException("Error reading input files");
			}
			firstFile = false;
		}
		
		out.flush();
		try {
			listReader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		
	}
	

	
	
	private void processInputFile(String inputListLine, boolean firstFile,
			PrintStream out) throws IOException {

		File inputFile;
		BufferedReader br = null;
		
		try {
			inputFile = new File(inputListLine);
			br = new BufferedReader(new FileReader(inputFile),bufferSize);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int linesToSkip;
		if (firstFile)
			linesToSkip = stripFirst;
		else
			linesToSkip = stripSubsequent;
			
		while (linesToSkip > 0) {
			br.readLine();
			linesToSkip--;
		}
		
		// write the rest of the file
		String line = br.readLine();
		while (line != null) {
			out.println(line);
			line = br.readLine();
		}
		out.flush();
		
	}



	@SuppressWarnings("static-access")
	private static Options initializeCLIOptions() {
		Options options = new Options();
		
		Option outfile = OptionBuilder.withArgName("outfile")
							.hasArg()
							.withDescription("Output file")
							.isRequired()
							.create('o');
							
		options.addOption(outfile);
		
		Option fileList = OptionBuilder.withArgName("infiles")
							.hasArg()
							.withDescription("File containing list of .csv filenames")
							.isRequired()
							.create('i');
		options.addOption(fileList);

		Option stripFirst = OptionBuilder.withArgName("stripFirst")
								.hasArg()
								.withDescription("Lines to strip from the first file")
								.isRequired()
								.create("stripFirst");
		options.addOption(stripFirst);
		
		Option stripSubsequent = OptionBuilder.withArgName("stripFirst")
				.hasArg()
				.withDescription("Lines to strip from the first file")
				.isRequired()
				.create("stripSubsequent");
		options.addOption(stripSubsequent);
		
		return options;
	}
	
}
