package abce.agency.firm;


import abce.agency.ec.*;
import abce.agency.ec.ecj.*;
import abce.agency.finance.*;
import abce.agency.firm.sr.*;
import ec.*;



public class ECJSimpleFirm extends SimpleFirm implements ECJEvolvableAgent {

	/**
	 * 
	 */
	private static final long			serialVersionUID	= 1L;
	Class<? extends StimulusResponse>[]	stimulus_responses	= null;
	AgencyGPIndividual					individual;
	EvolutionState						state;

	public static final double			UNDEFINED			= Double.MAX_VALUE * -1.0;



	public ECJSimpleFirm() {
		super(UNDEFINED); // We pay you (but should be set before the simulation
							// runs);
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



	@Override
	public void register(EvolutionState state, AgencyGPIndividual ind, Class<? extends StimulusResponse>[] sr) {
		this.state = state;
		individual = ind;
		stimulus_responses = sr;
		ind.setAgent(this);
	}



	@Override
	protected void price() {
		System.err.println("Entering ECJSimpleFirm price()");
		emit(new ECJSimpleFirmPriceSR(this));
	}



	@Override
	protected void produce() {
		super.produce();

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
