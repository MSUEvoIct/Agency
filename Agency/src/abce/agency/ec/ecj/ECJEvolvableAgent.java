package abce.agency.ec.ecj;


import abce.agency.ec.*;
import ec.gp.*;



public interface ECJEvolvableAgent extends EvolvableAgent {

	public void register(GPIndividual ind, Class<? extends StimulusResponse>[] sr);

}
