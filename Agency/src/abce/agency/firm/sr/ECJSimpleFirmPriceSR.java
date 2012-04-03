package abce.agency.firm.sr;


import javax.xml.ws.*;

import abce.agency.ec.*;
import abce.agency.finance.*;
import abce.agency.firm.*;
import evoict.reflection.*;



public class ECJSimpleFirmPriceSR implements StimulusResponse {

	static final RestrictedMethodDictionary	static_dict	=
																new RestrictedMethodDictionary(
																		ECJSimpleFirmPriceSR.class, 3);

	@Stimulus(name = "Firm")
	public ECJSimpleFirm					_firm;

	@Stimulus(name = "Account")
	public Accounts							_account;



	public ECJSimpleFirmPriceSR(ECJSimpleFirm firm) {
		_firm = firm;
		_account = firm.getAccounts();
	}



	@Action
	public void setPrice(double new_price) {
		_firm.setPrice(new_price);
	}



	@Override
	public MethodDictionary dictionary() {
		return static_dict;
	}
}