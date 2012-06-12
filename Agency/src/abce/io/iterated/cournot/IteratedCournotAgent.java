package abce.io.iterated.cournot;

import sim.engine.Steppable;
import ec.Fitness;
import ec.Individual;

public interface IteratedCournotAgent extends Steppable {

	/**
	 * Agents must determine production, and keep a record of previous
	 * production
	 * 
	 * @param stepsAgo
	 * @return
	 */
	public double getProduction(int stepsAgo);

	/**
	 * Market clearing is determined simultaneously, so the simulation must tell
	 * the agents how much they revenue they receive each step.
	 * 
	 * @param revenue
	 * @return
	 */
	public void earnRevenue(double revenue);
	
	public double getTotalRevenue();
	
	public Fitness getFitness();
	
	public Individual getIndividual();
	
}
