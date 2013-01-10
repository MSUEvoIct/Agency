package ec.agency.iteratedcournot;

import sim.engine.Steppable;
import ec.Fitness;
import ec.Individual;

public interface IteratedCournotAgent {

	/**
	 * Agents must determine production
	 * 
	 * @return
	 */
	public float getProduction(ProductionStimulus prodStim);

	
}
