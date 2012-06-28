package abce.agency.ec.ecj;


import abce.agency.ec.StimulusResponse;
import ec.Problem;



public class StimulusResponseProblem extends Problem {

	private static final long			serialVersionUID	= 1L;

	protected final StimulusResponse	_sr;



	public StimulusResponseProblem(StimulusResponse sr) {
		_sr = sr;
	}



	public StimulusResponse retrieve() {
		return _sr;
	}
}
