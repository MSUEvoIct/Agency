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
	private int activeJobs = 0;
	
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

		ThreadPoolHelper helper = new ThreadPoolHelper(model);
		
		threadPool.submit(helper);
		
	}

	@Override
	public void finish() {
		/*
		 * Check every 0.1 seconds to see if the task queue is empty. Print
		 * status messages for debug purposes.
		 * 
		 */
		
		while(activeJobs != 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		System.out.println("All Simulations Finished.");

	}

	private class ThreadPoolHelper implements Runnable {

		Runnable model;
		
		ThreadPoolHelper(final Runnable model) {
			this.model = model;
		}
		
		@Override
		public void run() {
			lock.lock();
			activeJobs++;
			lock.unlock();

			model.run();
			
			lock.lock();
			activeJobs--;
			lock.unlock();
		}
		
	}
	

}
