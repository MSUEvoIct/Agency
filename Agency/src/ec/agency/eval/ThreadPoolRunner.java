package ec.agency.eval;

import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class ThreadPoolRunner implements AgencyRunner {
	public static final long serialVersionUID = 1L;

	// Initialization / parameter information from ECJ
	private EvolutionState evoState = null;
	private Parameter base = null;

	// Our simulation queue and task pool
	private static LinkedBlockingQueue<Runnable> tasks = null;
	private static ThreadPoolExecutor threadPool = null;

	private final Lock lock = new ReentrantLock();
	private final Condition queueCleared = lock.newCondition();

	/**
	 * The queue of simulations in place for this threadpool will be this number
	 * times the ECJ evalthreads parameter.
	 */
	private static final int queueSizeMultiplier = 2;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		this.evoState = state;
		this.base = base;

		// Do first time setup
		if (tasks == null) {
			int numThreads = state.parameters.getInt(new Parameter(
					"evalthreads"), null);
			tasks = new LinkedBlockingQueue<Runnable>(numThreads
					* queueSizeMultiplier);

			threadPool = new ThreadPoolExecutor(numThreads, numThreads,
					Long.MAX_VALUE, TimeUnit.NANOSECONDS, tasks);

		}

	}

	@Override
	public void runSimulations(GroupCreator gc, FitnessListener fl) {
		int simulationID = 0;
		System.out.println("Starting evaluation of simulations");
		
		/*
		 * Schedule all the simulations. This loop should block on task.add()
		 * when the queue size full. After this while loop finishes all
		 * simulations should be scheduled for execution; all that's necessary
		 * is to wait for them to finish before allowing execution to return to
		 * the Evaluator for fitness aggregation.
		 */
		while (gc.hasNext()) {
			Set<Individual> group = gc.next();
			AgencyModel sim = AgencyEvaluator.getModel(evoState, base);
			sim.addFitnessListener(fl);

			for (Individual ind : group) {
				sim.addIndividual(ind);
			}

			sim.setGeneration(evoState.generation);
			sim.setSimulationID(simulationID++);

			// Wait for space to open up in the queue
			while (tasks.remainingCapacity() < 1) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			threadPool.execute(sim);
		}
		// All simulations have now been submitted to the queue.
		
		/*
		 * Check every 0.1 seconds to see if the task queue is empty. Print
		 * status messages for debug purposes.
		 * 
		 * TODO: Make sure that items are only removed from the queue once
		 * execution is *complete*. If the queue is empty only because running
		 * jobs are somewhere else, then an empty queue doesn't mean that all
		 * jobs have finished.
		 */
		System.out.println("Waiting for all simulations to finish");
		int timesWaited = 0;
		while (!tasks.isEmpty()) {
			if (timesWaited % 300 == 0)
				System.out.println("Waited " + timesWaited / 10 + " seconds");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			timesWaited++;
		}
		System.out.println("All Simulations Finished.");

	}

}
