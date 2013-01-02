package abce.agency.ec.ecj;

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
public interface AgencyECJSimulation extends ec.Setup, Runnable, FitnessUpdater {

	/**
	 * Sets the random seed of the agent based model. This functionality is
	 * required for repeatable experiments.
	 * 
	 * @param seed
	 */
	public void setSeed(int seed);

	/**
	 * The function of agent based models in the EC context is to evaluate the
	 * fitness of individual agents. In order to do that, ECJ must send the
	 * individuals to the agent based model, and that model must accept those
	 * individuals.
	 * 
	 * @param ind
	 */
	public void addIndividual(Individual ind);
	
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
