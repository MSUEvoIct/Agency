package abce.agency.util;

import ec.util.MersenneTwisterFast;


public class RandomGenerator extends MersenneTwisterFast {

	private static final long	serialVersionUID	= 1L;



	public RandomGenerator() {
		super();
	}



	public RandomGenerator(final long seed)
	{
		setSeed(seed);
	}



	public RandomGenerator(final int[] array)
	{
		setSeed(array);
	}



	/**
	 * Get the number of successes out of n trials with
	 * probability p of success.
	 * 
	 * @param n
	 *            trials
	 * @param p
	 *            successes
	 * @return
	 */
	public int getBinomial(int n, double p) {
		// This algorithm can be improved for large values of n
		// (see Kachitvichyanukul & Schmeiser 1988, ACM 31:2)
		int suc = 0;
		int k = 0;
		do {
			double unif = nextDouble();
			k++;
			if (unif <= p)
				suc++;
		} while (k < n);
		return suc;
	}
}
