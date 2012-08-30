package abce.agency.ec.ecj;

import ec.Individual;

public interface AgencyECJSimulation extends ec.Setup, Runnable, FitnessUpdater {
	public void setSeed(int seed);
	public void addIndividual(Individual ind);
}
