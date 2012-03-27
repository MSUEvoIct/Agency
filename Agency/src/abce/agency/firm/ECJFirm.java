package abce.agency.firm;


import abce.agency.ec.*;
import abce.agency.ec.ecj.*;
import ec.gp.*;



public class ECJFirm extends Firm implements ECJEvolvableAgent {

	Class<? extends StimulusResponse>[]	stimulus_responses	= null;
	GPIndividual						individual;



	@Override
	public void emit(StimulusResponse sr) {
		for (int k = 0; k < stimulus_responses.length; k++) {
			if (sr.getClass().isAssignableFrom(stimulus_responses[k])) {
				StimulusResponseProblem sr_problem = StimulusResponseProblemFactory.build(sr);
				individual.trees[k].child.eval(null, 0, null, null, individual, sr_problem);
			}
		}

	}



	public void register(GPIndividual ind, Class<? extends StimulusResponse>[] sr) {
		// TODO Auto-generated method stub

	}



	@Override
	public void register(GPIndividual ind, Class<? extends StimulusResponse> sr) {
		// TODO Auto-generated method stub

	}



	@Override
	protected void price() {
		// TODO Auto-generated method stub

	}



	@Override
	protected void produce() {
		// TODO Auto-generated method stub

	}

}
