package ec.agency;


/**
 * Contains information about how long it took a collection of items to execute.
 * It can be used, for example, to measure how long it takes problems to
 * execute.
 */
public class EvalThreadStats {

	// All times are in milliseconds
	public long	maximum_time	= Long.MIN_VALUE;
	public long	minimum_time	= Long.MAX_VALUE;
	public long	total_time		= 0;
	public long	num_evaluations	= 0;
}
