package abce.ecj;


import java.util.*;
import java.util.concurrent.*;

import ec.*;
import ec.util.*;



/**
 * A GroupedEvaluator uses an EvaluationGrouper to create sets of individuals
 * that will be evaluated together in the problem. Each EvaluationGroup produced
 * by the EvaluationGrouper gets placed into a queue established for each
 * evaluation thread. The GroupedEvaluatorAssistant class handles the execution
 * of problems for each evaluation thread and EvaluationGroup queue.
 * 
 * @author ruppmatt
 * 
 */
public class GroupedEvaluator extends Evaluator {

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;

	// How long should the evaluator let the problem threads run?
	public long						lifetime_minutes;

	// The grouper produces sets of agents to be evaluated together by the
	// problem.
	public EvaluationGrouper		grouper;

	public static final String		P_TIMEOUT			= "timeout_sec";
	public static final String		P_GROUPER			= "grouper";

	ArrayList<EvaluationGroup>[]	last_results		= null;



	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		lifetime_minutes = state.parameters.getInt(base.push(P_TIMEOUT), null);
		grouper = (EvaluationGrouper) state.parameters.getInstanceForParameter(base.push(P_GROUPER), null,
				EvaluationGrouper.class);
		grouper.setup(state, base.push(P_GROUPER));
	}



	public void reset() {
		last_results = null;
	}



	@Override
	public void evaluatePopulation(EvolutionState state) {

		reset();
		int nthreads = state.evalthreads;

		@SuppressWarnings("unchecked")
		ArrayList<EvaluationGroup> to_eval[] = new ArrayList[nthreads];
		boolean done[] = new boolean[nthreads];

		for (int thr = 0; thr < state.evalthreads; thr++) {
			to_eval[thr] = new ArrayList<EvaluationGroup>();
			done[thr] = false;
		}

		grouper.prepareGrouper(state, 0);
		int thr = 0;
		int queues_done = 0;
		while (queues_done < nthreads) {
			EvaluationGroup group = grouper.next(state, 0, thr);
			if (group == null && done[thr] == false) {
				done[thr] = true;
				queues_done++;
				continue;
			}
			if (group != null) {
				to_eval[thr].add(group);
			}
			thr = (thr + 1) % state.evalthreads;
		}
		evaluate(state, to_eval);
		last_results = to_eval;
	}



	/**
	 * Spawns assistant threads to evaluate each queue of evaluation groups in
	 * their own thread.
	 * 
	 * @param q
	 *            An array of queues containing evaluation groups. One queue
	 *            gets assigned to each evaluation thread.
	 */
	public void evaluate(final EvolutionState state, ArrayList<EvaluationGroup>[] q) {

		int qlen = q.length;
		((CallableGroupProblemForm) p_problem).preprocessPopulation(state, 0);

		// The thread pool will contain a single thread for each eval thread
		ExecutorService eval_service = Executors.newFixedThreadPool(qlen);

		// Build an evaluator assistant for each eval thread
		List<GroupedEvaluatorAssistant> eval_assist = new ArrayList<GroupedEvaluatorAssistant>(qlen);
		for (int thread = 0; thread < qlen; thread++) {
			CallableGroupProblemForm prob = (CallableGroupProblemForm) (p_problem.clone());
			eval_assist.add(new GroupedEvaluatorAssistant(state, q[thread], thread, prob, lifetime_minutes));
		}

		try {
			// invokeAll will block until all thread assistant's Future.isDone
			// returns true for all futures in the list
			List<Future<EvalThreadStats>> thread_stats = eval_service.invokeAll(eval_assist);

			// Display timing information for problem evaluation
			try {
				long total_sec = 0;
				long total_evals = 0;
				long min_prob_time = Long.MAX_VALUE;
				long max_prob_time = 0;
				for (Future<EvalThreadStats> l : thread_stats) {
					EvalThreadStats stats = l.get();
					total_sec += stats.total_time;
					total_evals += stats.num_evaluations;
					min_prob_time = (min_prob_time > stats.minimum_time) ? stats.minimum_time : min_prob_time;
					max_prob_time = (max_prob_time < stats.maximum_time) ? stats.maximum_time : max_prob_time;
				}
				StringBuilder msg = new StringBuilder();
				msg.append("Number of evaluations:   " + total_evals + "\n");
				msg.append("Average evaluation time: " + String.format("%.3f", total_sec / 1000.0) + " sec\n");
				msg.append("Minimum evaluation time: " + String.format("%.3f", min_prob_time / 1000.0) + " sec\n");
				msg.append("Maximum evaluation time: " + String.format("%.3f", max_prob_time / 1000.0) + " sec\n");
				state.output.message(msg.toString());
			} catch (InterruptedException e) {
				e.printStackTrace();
				state.output.fatal("Evaluator thread interrupted; this shouldn't happen.");
			} catch (ExecutionException e) {
				// Something went wrong with the evaluation thread; the
				// experiment should abort.
				e.getCause().getMessage();
				e.getCause().printStackTrace();
				e.printStackTrace();
				state.output.fatal("There was a problem evaluating the problem.  Aborting.");

			}

		} catch (InterruptedException e) {
			// handle exceptions for invokeAll
			state.output.fatal("Evaluator thread interrupted; this shouldn't happen.");
		}
		// Shutdown the evaluation service to release resources
		eval_service.shutdownNow();
	}



	@Override
	public boolean runComplete(EvolutionState state) {
		return false;
	}

	/**
	 * The GroupedEvaluatorAssistant contains eval thread specific information
	 * about the individuals and problems that need to be evaluated. Each
	 * assistant runs in its own thread.
	 * 
	 * @author ruppmatt
	 * 
	 */
	class GroupedEvaluatorAssistant implements Callable<EvalThreadStats> {

		final EvolutionState		state;
		final int					threadnum;
		CallableGroupProblemForm	prob;
		ArrayList<EvaluationGroup>	groups;
		final long					timeout;
		int							cur_ndx	= 0;



		/**
		 * 
		 * @param state
		 *            The evolution state (should not be modified)
		 * @param groups
		 *            A queue of groups of individuals that need to be evaluated
		 * @param threadnum
		 *            ECJ thread number
		 * @param prob
		 *            The problem to evaluate
		 * @param timeout
		 *            The maximum time the evaluator should wait for a problem
		 *            to finish.
		 */
		public GroupedEvaluatorAssistant(final EvolutionState state, ArrayList<EvaluationGroup> groups,
				int threadnum,
				CallableGroupProblemForm prob, long timeout) {
			this.state = state;
			this.groups = groups;
			this.threadnum = threadnum;
			this.prob = prob;
			this.timeout = timeout;
		}



		@Override
		public EvalThreadStats call() throws Exception {

			// Collect timing statistics about the problem instances
			EvalThreadStats stats = new EvalThreadStats();

			// Evaluate each EvaluationGroup for this thread
			for (EvaluationGroup g : groups) {
				long start_time = System.currentTimeMillis();

				// Need to set the problem up since call() takes no methods
				prob.setupForEvaluation(state, g, threadnum);

				// Make a FutureTask out of the CallableGroupProblemForm and run
				// it; throw an exception if it times out
				FutureTask<Object> task = new FutureTask<Object>(prob);
				task.run();
				task.get(timeout, TimeUnit.SECONDS);

				long task_time = System.currentTimeMillis() - start_time;

				stats.total_time = task_time;
				stats.maximum_time = (stats.maximum_time < task_time) ? task_time : stats.maximum_time;
				stats.minimum_time = (stats.minimum_time > task_time) ? task_time : stats.minimum_time;
				stats.num_evaluations++;
			}
			return stats;
		}
	}

}
