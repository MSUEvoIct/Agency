package abce.agency.firm;


import abce.agency.ec.*;
import abce.agency.ec.ecj.*;
import abce.agency.finance.*;
import abce.agency.firm.sr.*;



public class ECJSimpleFirm extends SimpleFirm implements ECJEvolvableAgent {

	/**
	 * 
	 */
	private static final long			serialVersionUID	= 1L;
	Class<? extends StimulusResponse>[]	stimulus_responses	= null;
	AgencyGPIndividual					individual;



	public ECJSimpleFirm(double price) {
		super(price);
	}



	@Override
	public void emit(StimulusResponse sr) {
		for (int k = 0; k < stimulus_responses.length; k++) {
			if (sr.getClass().isAssignableFrom(stimulus_responses[k])) {
				StimulusResponseProblem sr_problem = StimulusResponseProblemFactory.build(sr);
				individual.trees[k].child.eval(null, 0, null, null, individual, sr_problem);
			}
		}

	}



	@Override
	public void register(AgencyGPIndividual ind, Class<? extends StimulusResponse>[] sr) {
		individual = ind;
		stimulus_responses = sr;
		ind.setAgent(this);
	}



	@Override
	protected void price() {
		emit(new ECJSimpleFirmPriceSR(this));
	}



	@Override
	protected void produce() {
		// TODO Auto-generated method stub

	}



	public void setPrice(double p) {
		price = p;
	}



	public Accounts getAccounts() {
		return accounts;
	}



	@Override
	public double getFitness() {
		return accounts.getNetWorth();
	}

}
