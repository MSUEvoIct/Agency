package abce.agency.firm;


import abce.agency.ec.*;
import abce.agency.ec.ecj.*;
import ec.*;



public class ECJFirm extends ECFirm implements ECJEvolvableAgent {

	/**
	 * 
	 */
	private static final long			serialVersionUID	= 1L;
	Class<? extends StimulusResponse>[]	stimulus_responses	= null;
	AgencyGPIndividual					individual;
	EvolutionState						state;



	public ECJFirm() {
		super();
	}



	@Override
	public void emit(StimulusResponse sr) {
		for (int k = 0; k < stimulus_responses.length; k++) {
			if (sr.getClass().isAssignableFrom(stimulus_responses[k])) {
				StimulusResponseProblem sr_problem = StimulusResponseProblemFactory.build(sr);
				individual.trees[k].child.eval(state, 0, null, null, individual, sr_problem);
			}
		}

	}



	@SuppressWarnings("unchecked")
	@Override
	public void register(EvolutionState state, AgencyGPIndividual ind, Class<? extends StimulusResponse>[] sr) {
		this.state = state;
		individual = ind;
		stimulus_responses = sr;
		for (Class<? extends StimulusResponse> s : sr) {
			addSR(s);
		}
		ind.setAgent(this);
	}



	@Override
	public double getFitness() {
		return accounts.getNetWorth();
	}

}
