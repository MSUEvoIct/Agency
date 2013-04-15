package ec.agency.io;

import java.util.ArrayList;

import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import ec.util.MersenneTwisterFast;

public class GenerationAggregatingDataOutputFile extends DataOutputFile {

	int currentGeneration;
	int numCols;

	double[] min;
	Mean[] mean;
	StandardDeviation[] stdDev;
	Kurtosis[] kurtosis;
	Skewness[] skew;
	double[] max;
	int[] n;

	public GenerationAggregatingDataOutputFile(String fileName,
			String[] colNames) {
		super(fileName); // this includes shutdown hook
		
		ArrayList<String> expandedColNames = new ArrayList<String>();

		if (colNames != null) {
			for (int i = 0; i < colNames.length; i++) {
				if (i == 0) {
					// Keep the first col (generation) the same.
					expandedColNames.add(colNames[0]);
				} else {
					expandedColNames.add(colNames[i] + "_min");
					expandedColNames.add(colNames[i] + "_mean");
					expandedColNames.add(colNames[i] + "_stddev");
					expandedColNames.add(colNames[i] + "_kurtosis");
					expandedColNames.add(colNames[i] + "_skew");
					expandedColNames.add(colNames[i] + "_max");
					expandedColNames.add(colNames[i] + "_n");
				}
			}
		} else {
			throw new RuntimeException(this.getClass().getSimpleName()
					+ " requires column names");
		}

		super.writeTuple(expandedColNames.toArray());


		numCols = colNames.length;
		resetStats(numCols);

	}

	@Override
	public void close() {
		outputAndReset();
		super.close();
	}

	private void outputAndReset() {
		// Write data
		int objSize = ((numCols - 1) * 7) + 1;
		ArrayList<Object> toOutput = new ArrayList<Object>(objSize);
		toOutput.add(this.currentGeneration);
		for (int i = 1; i < numCols; i++) {
			toOutput.add(min[i]);
			toOutput.add(mean[i].getResult());
			toOutput.add(stdDev[i].getResult());
			toOutput.add(kurtosis[i].getResult());
			toOutput.add(skew[i].getResult());
			toOutput.add(max[i]);
			toOutput.add(n[i]);
		}
		super.writeTuple(toOutput.toArray());

		// Reset stats
		resetStats(numCols);
	}

	@Override
	public synchronized void writeTuple(Object[] v) {
		// assumes variables[0] is always generation
		int thisGen = (Integer) v[0];

		if (thisGen != currentGeneration) {

			outputAndReset();
			
			// Update generation
			currentGeneration = thisGen;

		}

		for (int i = 1; i < v.length; i++) {
			Number number = (Number) v[i];
			double realValue = number.doubleValue();

			// update min
			if (realValue < min[i])
				min[i] = realValue;
			// update max
			if (realValue > max[i])
				max[i] = realValue;
			// update n
			n[i]++;
			// update others
			mean[i].increment(realValue);
			stdDev[i].increment(realValue);
			kurtosis[i].increment(realValue);
			skew[i].increment(realValue);

		}

	}

	private void resetStats(int size) {
		min = new double[size];
		mean = new Mean[size];
		stdDev = new StandardDeviation[size];
		kurtosis = new Kurtosis[size];
		skew = new Skewness[size];
		max = new double[size];
		n = new int[size];

		// Create blank objects for stats. Java initializes primitive types
		// to 0 automagically.
		for (int i = 0; i < size; i++) {
			mean[i] = new Mean();
			stdDev[i] = new StandardDeviation();
			kurtosis[i] = new Kurtosis();
			skew[i] = new Skewness();
		}
	}

	public static void main(String args[]) {
		String[] colNames = new String[3];
		colNames[0] = "Generation";
		colNames[1] = "testvar1";
		colNames[2] = "testvar2";
		GenerationAggregatingDataOutputFile gadof = new GenerationAggregatingDataOutputFile(
				"test.tsv", colNames);

		MersenneTwisterFast random = new MersenneTwisterFast();
		for (int gen = 0; gen < 30; gen++) {
			for (int i = 0; i < 1000; i++) {
				Object[] toOut = new Object[3];
				toOut[0] = gen;
				toOut[1] = random.nextDouble();
				toOut[2] = random.nextInt(100);
				gadof.writeTuple(toOut);
			}
		}

		System.exit(0);

	}

}
