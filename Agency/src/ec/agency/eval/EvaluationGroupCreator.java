package ec.agency.eval;


import ec.EvolutionState;
import ec.Prototype;
import ec.util.Parameter;



/**
 * An evaluation grouper produces EvaluationGroups, which are used to store
 * information about which individuals in the Population should be included in a
 * Problem.
 * 
 * EvaluateGrouper should have its prepareGrouper(..) method called once prior
 * to requesting members.
 * 
 * next() will return either an EvaluationGroup or null if there are no more
 * groups to be created.
 * 
 * It will be the grouper's responsibility to ensure that any Individuals that
 * require updating during the domain execution are placed in the proper queue
 * to ensure concurrency safety, though synchronized components of Individuals
 * are also a way to ensure safety.
 * 
 * @author ruppmatt
 * 
 */
public abstract class EvaluationGroupCreator implements Prototype {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;



	@Override
	public void setup(EvolutionState state, Parameter base) {
	}



	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}



	@Override
	public Parameter defaultBase() {
		return EvaluationGrouperDefaults.base();
	}



	/**
	 * Reset the grouper; useful for reuse or cloning.
	 */
	public abstract void reset();



	/**
	 * Prepare the grouper to start producing individuals
	 * 
	 * @param state
	 *            EvolutionState instance
	 * @param calling_thread
	 *            Thread number making the request
	 */
	public abstract void prepareGrouper(final EvolutionState state, final int calling_thread);



	/**
	 * Return the next EvaluationGroup to be evaluated. null will be returned if
	 * there are no more groups to be evaluated.
	 * 
	 * @param state
	 *            EvolutionState instance
	 * @param calling_thread
	 *            thread number making the request
	 * @param for_evaluation_thread
	 *            thread number that will be used for the evaluation
	 * @return
	 *         EvaluationGroup or null if no more groups are necessary
	 */
	public abstract EvaluationGroup next(final EvolutionState state, final int calling_thread,
			final int for_evaluation_thread);

}
