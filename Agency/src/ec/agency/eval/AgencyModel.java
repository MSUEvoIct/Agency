package ec.agency.eval;

import java.util.Map;

import ec.Fitness;
import ec.Individual;

/**
 * In order for ECJ to run agent-based models as part of its population
 * evaluation process, it needs to be able to pass certain information on to the
 * simulation instance. This interface, specifically addIndividual() and
 * getFitnesses(), allows it to do that.
 * 
 * @author kkoning
 * 
 */
public interface AgencyModel extends ec.Setup, Runnable {

	/**
	 * Sets the random seed of the agent based model. This functionality is
	 * required for repeatable experiments.
	 * 
	 * @param seed
	 */
	public void setSeed(int seed);

	/**
	 * Contains the ec.Individual's to be evaluated
	 * 
	 * @param evalGroup
	 */
	public void setEvaluationGroup(EvaluationGroup evalGroup);
	
	/**
	 * Provides the agent based model with information that is likely 
	 * necessary for data output purposes.
	 * 
	 * @param generation
	 */
	public void setGeneration(Integer generation);
	public Integer getGeneration();

	/**
	 * Provides the agent based model with information that is likely 
	 * necessary for data output purposes.
	 * 
	 * @param simulationID
	 */
	public void setSimulationID(Integer simulationID);
	public Integer getSimulationID();

	/**
	 * @return a map giving a fitness for every individual within the
	 * simulation.
	 */
	public Map<Individual,Fitness> getFitnesses();
	
	
}
