package abce.agency.ec.ecj;


import abce.agency.ec.*;
import ec.*;
import ec.gp.*;



public interface ECJEvolvableAgent extends EvolvableAgent {

	public void register(EvolutionState state, int threadnum, GPIndividual ind,
			Class<? extends StimulusResponse>[] sr);

}
