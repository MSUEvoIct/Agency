package ec.agency;


import ec.EvolutionState;
import ec.gp.GPIndividual;



public interface ECJEvolvableAgent extends EvolvableAgent {

	public void register(EvolutionState state, int threadnum, GPIndividual ind,
			Class<? extends StimulusResponse>[] sr);

}
