package abce.agency.firm;


import javax.xml.ws.*;

import abce.agency.ec.*;
import abce.agency.ec.ecj.*;
import abce.agency.finance.*;
import ec.gp.*;
import evoict.reflection.*;



public class ECJSimpleFirm extends SimpleFirm implements ECJEvolvableAgent {

	/**
	 * 
	 */
	private static final long			serialVersionUID	= 1L;
	Class<? extends StimulusResponse>[]	stimulus_responses	= null;
	GPIndividual						individual;



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
	public void register(GPIndividual ind, Class<? extends StimulusResponse>[] sr) {
		individual = ind;
		stimulus_responses = sr;

	}



	@Override
	protected void price() {
		emit(new FirmPriceSR(this));
	}



	@Override
	protected void produce() {
		// TODO Auto-generated method stub

	}

}



class FirmPriceSR implements StimulusResponse {

	static final RestrictedMethodDictionary	static_dict	=
																new RestrictedMethodDictionary(FirmPriceSR.class, 3);

	@Stimulus(name = "Firm")
	public ECJSimpleFirm					_firm;

	@Stimulus(name = "Account")
	public Accounts							_account;



	public FirmPriceSR(ECJSimpleFirm firm) {
		_firm = firm;
		_account = firm.accounts;
	}



	@Action
	public void setPrice(double new_price) {
		_firm.price = new_price;
	}



	@Override
	public MethodDictionary dictionary() {
		return static_dict;
	}

}
