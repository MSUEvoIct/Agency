package abce.agency.ec.ecj;


import abce.agency.ec.*;



public interface ECJEvolvableAgent extends EvolvableAgent {

	public void register(AgencyGPIndividual ind, Class<? extends StimulusResponse>[] sr);

}
