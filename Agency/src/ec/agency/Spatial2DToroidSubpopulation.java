package ec.agency;


import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ec.EvolutionState;
import ec.Subpopulation;
import ec.spatial.Space;
import ec.util.Parameter;



/**
 * Impose a 2-dimensional toroid structure upon a populatioin for spatial
 * breeding and evaluation.
 * 
 * @author ruppmatt
 * 
 */
public class Spatial2DToroidSubpopulation extends Subpopulation implements Space {

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;

	// Size of the lattice along the x-axis
	public static final String		P_XSIZE				= "xsize";

	// Size of the lattice along the y-axis
	public static final String		P_YSIZE				= "ysize";

	// Spatial information is maintained by retrieving the index to an
	// individual in the subpopulation based on the evaluator or breeder thread
	// that is being used.
	public int[]					thread_to_ind		= null;
	public int						x_size;
	public int						y_size;
	public ReentrantReadWriteLock	lock				= new ReentrantReadWriteLock();



	/**
	 * Setup the grid size of the lattice and make sure it matches the number of
	 * individuals in the subpopulation
	 */
	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		Parameter def = defaultBase();
		x_size = state.parameters.getInt(base.push(P_XSIZE), def.push(P_XSIZE), 1);
		y_size = state.parameters.getInt(base.push(P_YSIZE), def.push(P_YSIZE), 1);

		if (x_size <= 0) {
			state.output
					.fatal("Toroid subpopulation requires an x size > 0.", base.push(P_XSIZE), def.push(P_XSIZE));
		}
		if (y_size <= 0) {
			state.output.fatal("2D toroid subpopulation requires a y size > 0.", base.push(P_YSIZE), def.push(P_YSIZE));
		}
		if (x_size * y_size < individuals.length) {
			state.output
					.fatal("Toroid subpopulation requires the product x and y sizes to equal the number of individuals.");
		}
	}



	/**
	 * Set the index to the individual of interest for this thread. This should
	 * be called everytime a new thread breeds or evaluates individuals from
	 * this subpopulation.
	 * 
	 * @param threadnum
	 *            thread number key
	 * @param index
	 *            index of individual
	 */
	@Override
	public void setIndex(int threadnum, int ind_ndx) {
		// Need to ensure that multiple threads aren't editing the thread_to_ind
		// at the same time
		lock.writeLock().lock();
		try {
			if (thread_to_ind == null) {
				thread_to_ind = new int[threadnum + 1];
				Arrays.fill(thread_to_ind, -1);
			} else if (threadnum > thread_to_ind.length - 1) {
				int[] resized = new int[threadnum + 1];
				Arrays.fill(resized, -1);
				System.arraycopy(thread_to_ind, 0, resized, 0, thread_to_ind.length);
				thread_to_ind = resized;
			}
			thread_to_ind[threadnum] = ind_ndx;
		} finally {
			lock.writeLock().unlock();
		}
	}



	/**
	 * Return the index of an individual in this population given a particular
	 * thread; -1 is returned on an error.
	 * 
	 * 
	 * @param threadnum
	 *            the thread number to use as a key
	 * @return
	 *         the index of the individual or -1 on an error
	 */
	@Override
	public int getIndex(int threadnum) {
		lock.readLock().lock();
		try {
			return (threadnum < thread_to_ind.length) ? thread_to_ind[threadnum] : -1;
		} finally {
			lock.readLock().unlock();
		}
	}



	/**
	 * Return a random neighbor in the local lattice. Distance is ignored.
	 * Position on the lattice is laid out as:
	 * y+
	 * ^
	 * | 0 1 2
	 * | 3 C 4
	 * | 5 6 7
	 * ------>x+
	 */
	@Override
	public int getIndexRandomNeighbor(EvolutionState state, int threadnum, int distance) {
		int center_ndx = getIndex(threadnum);
		if (center_ndx < 0) {
			state.output
					.fatal("Spatial2DToroidSubpopulation received a request for a random neighbor in a thread that does not have a known spatial index.");
		} else if (center_ndx > individuals.length) {
			state.output
					.fatal("Spatial2DToroidSubpopulation received a request for a random neighbor; the index of the individual at the center of the index is out of bounds");
		}
		int pos = state.random[threadnum].nextInt(7); // 7 lattice positions
		int cx = center_ndx % y_size;
		int cy = center_ndx / y_size;
		int dx = 0;
		if (pos == 0 || pos == 3 || pos == 5) {
			dx = -1;
		} else if (pos == 2 || pos == 4 || pos == 7) {
			dx = 1;
		}
		int dy = 0;
		if (pos < 3) {
			dy = 1;
		} else if (pos > 4) {
			dy = -1;
		}
		int nx = cx + dx;
		int ny = cy + dy;
		int neighbor_ndx = ny * x_size + nx;
		return neighbor_ndx;
	}
}
