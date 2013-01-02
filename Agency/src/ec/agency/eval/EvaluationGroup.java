package ec.agency.eval;


import ec.Fitness;
import ec.Individual;



/**
 * An evaluation group is simply a container that holds which individuals in a
 * population should be included in a Problem, and which ones should have their
 * fitnesses evaluated.
 * 
 * @author ruppmatt
 * 
 */
public class EvaluationGroup {

	/**
	 *  The individuals to put in a problem together
	 */
	public Individual[]	ind;

	/**
	 *  The populations these individuals come from
	 */
	public int[]		subpops;

	/**
	 * Whether or not the should have their fitneses evaluated
	 */
	public boolean[]	evaluate_fitness;

	/**
	 * The next index to store information
	 */
	public int			actual_size;

	/**
	 * Fitness information (even if not stored in the individual)
	 */
	public Fitness[]	fitness;



	/**
	 * Create a fixed-size evaluation group
	 * 
	 * @param size
	 *            Number of individuals to include in the group
	 */
	public EvaluationGroup(int size) {
		ind = new Individual[size];
		subpops = new int[size];
		evaluate_fitness = new boolean[size];
		fitness = new Fitness[size];
		actual_size = 0;
	}



	/**
	 * Add a single individual to the group
	 * 
	 * @param i
	 * @param subpop
	 * @param eval_fit
	 * @throws RuntimeException
	 */
	public void add(Individual i, int subpop, boolean eval_fit) throws RuntimeException {
		if (actual_size < ind.length) {
			ind[actual_size] = i;
			subpops[actual_size] = subpop;
			evaluate_fitness[actual_size] = eval_fit;
			fitness[actual_size] = null;
			actual_size++;
		} else {
			throw new RuntimeException("Index out of bounds.");
		}
	}



	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Individuals:  ");
		for (int k = 0; k < ind.length; k++)
			buf.append(ind[k] + " ");
		buf.append(" ");
		buf.append("Subpops: ");
		for (int k = 0; k < subpops.length; k++)
			buf.append(subpops[k] + " ");
		buf.append(" ");
		buf.append("Evaluate: ");
		for (int k = 0; k < evaluate_fitness.length; k++)
			buf.append(evaluate_fitness[k] + " ");
		buf.append(" ");
		return buf.toString();

	}

}
