package ec.agency.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Random;

public class ReplicateCreator {

	static final int apParent = 0;
	static final int apNumReps = 1;

	static final String namePrefix = "rep";
	static final String repNumsFormat = "000";

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		String parent = args[0];
		int numReplicates = Integer.parseInt(args[1]);

		for (int i = 0; i < numReplicates; i++) {

			DecimalFormat df = new DecimalFormat(repNumsFormat);
			String dirName = namePrefix + df.format(i);
			File dir = new File(dirName);
			dir.mkdirs();

			String fileName = dirName + "/" + namePrefix + ".properties";
			File outFile = new File(fileName);
			
			OutputStream os = new FileOutputStream(outFile);
			PrintWriter repOut = new PrintWriter(os);
			repOut.println("parent.0 = ../" + parent);
			
			// Get seed and record it
			Random random = new Random();
			repOut.println("seed.0 = " + random.nextInt());
			
			repOut.flush();
			repOut.close();
			
			
			
		}

	}

}
