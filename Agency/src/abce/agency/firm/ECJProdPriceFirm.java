package abce.agency.firm;


import abce.agency.ec.*;
import abce.agency.ec.ecj.*;
import ec.*;



/**
 * ECJFirm implements the interface ECJEvolvableAgents on an ECFirm, providing
 * the ability of StimulusResponse objects to inform AgencyGPIndividuals and
 * alter the behavior of this ECJFirm from within ECJ.
 * 
 * @author ruppmatt
 * 
 */
public class ECJProdPriceFirm extends ECProdPriceFirm implements ECJEvolvableAgent {

	/**
	 * 
	 */
	private static final long			serialVersionUID	= 1L;
	Class<? extends StimulusResponse>[]	stimulus_responses	= null;
	AgencyGPIndividual					individual;

	// TODO: Move these to MarketSimulation [would require passing it around,
	// though, because it needs to get to emit() ]
	EvolutionState						state;
	int									threadnum;



	public ECJProdPriceFirm() {
		super();
	}



	@Override
	public void emit(StimulusResponse sr) {
		for (int k = 0; k < stimulus_responses.length; k++) {
			if (sr.getClass().isAssignableFrom(stimulus_responses[k])) {
				StimulusResponseProblem sr_problem = new StimulusResponseProblem(sr);
				individual.trees[k].child.eval(state, threadnum, null, null, individual, sr_problem);
			}
		}

	}



	@SuppressWarnings("unchecked")
	@Override
	public void register(EvolutionState state, int threadnum, AgencyGPIndividual ind,
			Class<? extends StimulusResponse>[] sr) {
		this.state = state;
		this.threadnum = threadnum;
		individual = ind;
		stimulus_responses = sr;
		for (Class<? extends StimulusResponse> s : sr) {
			addSR(s);
		}
	}



	@Override
	public double getFitness() {
		Float w = (float) accounts.getNetWorth();
		if (w.isNaN() || w.isInfinite())
			w = (float) 0.0;
		return w;
	}

}
