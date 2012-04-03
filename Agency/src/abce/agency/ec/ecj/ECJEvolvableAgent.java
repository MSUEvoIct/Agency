package abce.agency.ec.ecj;


import abce.agency.ec.*;
import ec.*;



public interface ECJEvolvableAgent extends EvolvableAgent {

	public void register(EvolutionState state, AgencyGPIndividual ind, Class<? extends StimulusResponse>[] sr);

}
