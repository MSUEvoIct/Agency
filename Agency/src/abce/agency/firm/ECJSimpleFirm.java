package abce.agency.firm;


import test.firm.SimpleFirm;
import abce.agency.*;
import abce.agency.ec.*;
import abce.agency.ec.ecj.*;
import abce.agency.finance.*;
import abce.agency.firm.sr.*;
import abce.agency.goods.*;
import ec.*;
import evoict.reflection.*;



public class ECJSimpleFirm extends SimpleFirm implements ECJEvolvableAgent {

	/**
	 * 
	 */
	private static final long			serialVersionUID	= 1L;
	Class<? extends StimulusResponse>[]	stimulus_responses	= null;
	AgencyGPIndividual					individual;
	EvolutionState						state;

	@Stimulus(name = "LastScaling")
	double								last_scale			= 1.0;

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



	@Stimulus(name = "Price")
	public double getPrice() {
		return price;
	}



	@Override
	protected void price() {
		for (Market m : this.markets) {
			for (Good g : this.goods) {
				emit(new ECJSimpleFirmPriceSR(this, m, g));
			}
		}
	}



	@Override
	protected void produce() {
		super.produce();
	}



	public void adjustPrice(double perc) {
		double scale = 1.0 + (perc / 100.0);
		// System.err.println("Scaling: " + scale);
		if (scale < 0.0) {
			last_scale = 1.0;
			return;
		} else {
			last_scale = scale;
			price = price * scale;
		}
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
