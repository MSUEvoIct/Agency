package ec.agency.eval;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ec.EvolutionState;
import ec.util.Parameter;

public class ThreadPoolRunner implements AgencyRunner {
	public static final long serialVersionUID = 1L;

	// Our simulation queue and task pool
	private BlockingQueue<Runnable> tasks = null;
	private ThreadPoolExecutor threadPool = null;

	private final Lock lock = new ReentrantLock();
	private final Condition queueCleared = lock.newCondition();

	// private final int bundleSize = 100;
	//
	// private int bundlePos = 0;
	// private RunBundle bundle = new RunBundle();
	//
	// public class RunBundle extends ArrayList<Runnable> implements Runnable {
	// private static final long serialVersionUID = 1L;
	//
	// @Override
	// public void run() {
	// for (Runnable task : tasks) {
	// if (task != null)
	// task.run();
	// }
	// }
	//
	// }

	/**
	 * The queue of simulations in place for this threadpool will be this number
	 * times the ECJ evalthreads parameter.
	 */
	private static final int queueSizeMultiplier = 30;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		// Do first time setup
		if (tasks == null) {
			int numThreads = state.parameters.getInt(new Parameter(
					"evalthreads"), null);
			tasks = new ArrayBlockingQueue<Runnable>(numThreads
					* queueSizeMultiplier);

			threadPool = new ThreadPoolExecutor(numThreads, numThreads,
					Long.MAX_VALUE, TimeUnit.NANOSECONDS, tasks);

		}

	}

	@Override
	public synchronized void runModel(Runnable model) {
		// Wait for space to open up in the queue
		while (tasks.remainingCapacity() < 1) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		threadPool.submit(model);
	}

	@Override
	public void finish() {
		/*
		 * Check every 0.1 seconds to see if the task queue is empty. Print
		 * status messages for debug purposes.
		 * 
		 * TODO: Make sure that items are only removed from the queue once
		 * execution is *complete*. If the queue is empty only because running
		 * jobs are somewhere else, then an empty queue doesn't mean that all
		 * jobs have finished.
		 */
//		System.out.println("Waiting for all simulations to finish");
//		int timesWaited = 0;

		while (!tasks.isEmpty() && threadPool.getActiveCount() > 0) {
//			if (timesWaited % 300 == 0)
//				System.out.println("Waited " + timesWaited / 10 + " seconds");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
//			timesWaited++;
		}
		System.out.println("All Simulations Finished.");

	}

}
