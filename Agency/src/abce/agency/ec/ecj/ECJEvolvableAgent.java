package abce.agency.ec.ecj;


import abce.agency.ec.EvolvableAgent;
import abce.agency.ec.StimulusResponse;
import ec.EvolutionState;
import ec.gp.GPIndividual;



public interface ECJEvolvableAgent extends EvolvableAgent {

	public void register(EvolutionState state, int threadnum, GPIndividual ind,
			Class<? extends StimulusResponse>[] sr);

}
