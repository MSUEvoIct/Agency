package abce.agency.ec.ecj;


import ec.gp.*;



public class AgencyGPIndividual extends GPIndividual {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private ECJEvolvableAgent	_agent;



	public ECJEvolvableAgent getAgent() {
		return _agent;
	}



	public void setAgent(ECJEvolvableAgent agent) {
		_agent = agent;
	}



	public void reset() {
		_agent = null;
	}

}
